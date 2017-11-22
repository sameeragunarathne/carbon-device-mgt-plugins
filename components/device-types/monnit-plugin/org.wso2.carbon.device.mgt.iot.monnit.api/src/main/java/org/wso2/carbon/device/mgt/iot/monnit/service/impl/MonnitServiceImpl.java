package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensor;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitDevice;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants.Constants;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.util.APIUtil;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.util.MonnitResponseUtil;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.util.TransportUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MonnitServiceImpl implements MonnitService {

    private static Log log = LogFactory.getLog(MonnitServiceImpl.class);

    @Override
    @GET
    @Path("/monnit/{token}/sensors")
    public Response getAllSensors(@PathParam("token")String token, @QueryParam("name") String name,
            @QueryParam("applicationID") String applicationId) {
        String response = getSensorsList(token, name, applicationId);
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @GET
    @Path("/monnit/{token}/gateways")
    public Response getAllGateways(@PathParam("token")String token, String name) {
        String response = APIUtil.getGatewayList(token, name);
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @POST
    @Path("/monnit/{token}/sensors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignSensor(@PathParam("token")String token, MonnitDevice device) {
        boolean response = registerDevice(token, device);
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @POST
    @Path("/monnit/{token}/gateways")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignGateway(@PathParam("token")String token, MonnitDevice device) {
        boolean response = registerDevice(token, device);
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @GET
    @Path("/monnit/auth-token")
    public Response getAuthToken(@QueryParam("username") String username, @QueryParam("password") String password) {
        String token = APIUtil.getAuthToken(username, password);
        return Response.status(Response.Status.OK.getStatusCode()).entity(token).build();
    }

    private boolean registerDevice(String token, MonnitDevice monnitDevice) {
        Map<String, String> paramMap = new HashedMap();
        paramMap.put(Constants.NETWORK_ID, monnitDevice.getNetworkID());
        paramMap.put(Constants.SENSOR_ID, monnitDevice.getSensorID());
        paramMap.put(Constants.CHECK_DIGIT, monnitDevice.getCheckDigit());
        String url = TransportUtil.getURI(Constants.REG_SENSOR_EP, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);

        if(responseObj != null && responseObj.getResult().equals("Success")) {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(monnitDevice.getSensorID());
            deviceIdentifier.setType(Constants.DEVICE_TYPE);
            try {
                if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                    return false;
                }
                Device device = new Device();
                device.setDeviceIdentifier(deviceIdentifier.getId());
                EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
                enrolmentInfo.setDateOfEnrolment(new Date().getTime());
                enrolmentInfo.setDateOfLastUpdate(new Date().getTime());
                enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);
                enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
                device.setName(monnitDevice.getCheckDigit());
                device.setType(Constants.DEVICE_TYPE);
                enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
                device.setEnrolmentInfo(enrolmentInfo);

                //adding device properties
                List<Device.Property> propertyList = new ArrayList<>();
                Device.Property deviceType = new Device.Property();
                deviceType.setName(Constants.TYPE);
                deviceType.setValue(monnitDevice.getType());
                Device.Property checkDigitProperty = new Device.Property();
                checkDigitProperty.setName(Constants.CHECK_DIGIT);
                checkDigitProperty.setValue(monnitDevice.getCheckDigit());

                if(monnitDevice.getType().equals("sensor")) {
                    Device.Property sensorName = new Device.Property();
                    deviceType.setName(Constants.SENSOR_NAME);
                    deviceType.setValue("");
                    Device.Property applicationId = new Device.Property();
                    deviceType.setName(Constants.APPLICATION_ID);
                    deviceType.setValue("");
                    Device.Property csNetId = new Device.Property();
                    deviceType.setName(Constants.CS_NET_ID);
                    deviceType.setValue("");
                    Device.Property currentReading = new Device.Property();
                    deviceType.setName(Constants.CURRENT_READING);
                    deviceType.setValue("");
                    Device.Property batteryLevel = new Device.Property();
                    deviceType.setName(Constants.BATTERY_LEVEL);
                    deviceType.setValue("");
                    Device.Property signalStrength = new Device.Property();
                    deviceType.setName(Constants.SIGNAL_STRENGTH);
                    deviceType.setValue("");
                    Device.Property alertsActive = new Device.Property();
                    deviceType.setName(Constants.ALERTS_ACTIVE);
                    deviceType.setValue("");

                    propertyList.add(sensorName);
                    propertyList.add(applicationId);
                    propertyList.add(csNetId);
                    propertyList.add(currentReading);
                    propertyList.add(batteryLevel);
                    propertyList.add(signalStrength);
                    propertyList.add(alertsActive);
                } else if (monnitDevice.getType().equals("gateway")) {

                }

                propertyList.add(deviceType);
                propertyList.add(checkDigitProperty);
                device.setProperties(propertyList);

                boolean status = APIUtil.getDeviceManagementService().enrollDevice(device);
                if(status) {
                    DeviceLocation location = monnitDevice.getLocation();
                    location.setDeviceIdentifier(deviceIdentifier);
                    APIUtil.updateDeviceLocation(location);
                }
                return status;
            } catch (DeviceManagementException e) {
                log.error("Exception occurred when registering device in IoT server.", e);
                return false;
            } catch (DeviceDetailsMgtException e) {
                log.error("Error occurred while updating Device Location.", e);
                return false;
            }
        } else {
            log.error("Exception occurred when registering device in iMonnit cloud.");
            return false;
        }
    }

    private String getSensorsList(String token, String name, String applicationId) {
        String response = "";
        Map<String, String> paramMap = new HashedMap();
        paramMap.put("name", name);
        paramMap.put("applicationID", applicationId);
        String url  = TransportUtil.getURI(Constants.SENSOR_LIST, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);
        try {
            MonnitResponseUtil responseUtil = new MonnitResponseUtil();
            List<ApiSensor> apiSensors = (List<ApiSensor>) responseUtil.generateResultObj(responseObj);
            for (ApiSensor sensor: apiSensors) {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
                deviceIdentifier.setId(sensor.getSensorId());
                deviceIdentifier.setType(Constants.DEVICE_TYPE);
                if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                    Device device = APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);
                    List<Device.Property> propertyList = device.getProperties();
                    for (Device.Property prop: propertyList) {
                       switch (prop.getName()) {
                       case Constants.SENSOR_NAME:
                           prop.setValue(sensor.getSensorName());
                           break;
                       case Constants.APPLICATION_ID:
                           prop.setValue(sensor.getApplicationId());
                           break;
                       case Constants.CURRENT_READING:
                           prop.setValue(sensor.getCurrentReading());
                           break;
                       case Constants.CS_NET_ID:
                           prop.setValue(sensor.getCsNetId());
                           break;
                       case Constants.BATTERY_LEVEL:
                           prop.setValue(sensor.getBatteryLevel());
                           break;
                       case Constants.SIGNAL_STRENGTH:
                           prop.setValue(sensor.getSignalStrength());
                           break;
                       case Constants.ALERTS_ACTIVE:
                           prop.setValue(sensor.isAlertsActive());
                           break;
                       }
                    }
                    device.setProperties(propertyList);
                    EnrolmentInfo.Status status;
                    if(sensor.getStatus().equals("1")) {
                        status = EnrolmentInfo.Status.ACTIVE;
                    } else {
                        status = EnrolmentInfo.Status.INACTIVE;
                    }
                    APIUtil.getDeviceManagementService().updateDeviceEnrolmentInfo(device, status);
                }
//                //registering sensor if not enrolled
//                MonnitDevice monnitDevice = new MonnitDevice();
//                paramMap = new HashedMap();
//                paramMap.put("sensorID", sensor.getSensorId());
//                url  = TransportUtil.getURI(Constants.SENSOR_LIST, token, paramMap);
//                monnitDevice.setNetworkID((String) APIUtil.getResponse(url).getResult());
//                monnitDevice.setSensorID(sensor.getSensorId());
//                monnitDevice.setCheckDigit(sensor.getCheckDigit());
//                registerDevice(token, monnitDevice);
            }
        } catch (JAXBException e) {
            log.error("Error occurred while unmarshalling document.", e);
        } catch (DeviceManagementException e) {
            log.error("Exception occurred when retrieving device from IoT server.", e);
        }
        return response;
    }
}
