package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceNotFoundException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupAlreadyExistException;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupManagementException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiGateway;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensor;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitDevice;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitDeviceGroup;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants.Constants;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.util.APIUtil;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.util.MonnitResponseUtil;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.util.TransportUtil;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
    @Path("/monnit/init")
    public Response init(@QueryParam("token") String token) {
        Map<String, String> paramMap = new HashedMap();
        String url  = TransportUtil.getURI(Constants.SENSOR_LIST, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);
        MonnitResponseUtil responseUtil = new MonnitResponseUtil();
        try {
            List<ApiSensor> apiSensors = (List<ApiSensor>) responseUtil.generateResultObj(responseObj);
            for (ApiSensor sensor : apiSensors) {
                MonnitDevice sensorDevice =  new MonnitDevice();
                sensorDevice.setSensorID(sensor.getSensorId());
                sensorDevice.setName(sensor.getSensorName());
                sensorDevice.setCheckDigit(sensor.getCheckDigit());
                sensorDevice.setType("sensor");
                registerDevice(sensorDevice);
            }
            addSensorProperties(apiSensors);

            url  = TransportUtil.getURI(Constants.GATEWAY_LIST, token, paramMap);
            responseObj = APIUtil.getResponse(url);
            List<ApiGateway> apiGateways = (List<ApiGateway>) responseUtil.generateResultObj(responseObj);
            for (ApiGateway gateway : apiGateways) {
                MonnitDevice gatewayDevice = new MonnitDevice();
                gatewayDevice.setGatewayID(gateway.getGatewayId());
                gatewayDevice.setName(gateway.getName());
                gatewayDevice.setCheckDigit(gateway.getCheckDigit());
                gatewayDevice.setType("gateway");
                registerDevice(gatewayDevice);
            }
            addGatewayProperties(apiGateways);
        } catch (JAXBException e) {
            String msg = "Error occurred while unmarshalling document.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        String msg = "Successfully synced with monnit cloud.";
        return Response.status(Response.Status.OK.getStatusCode()).entity(msg).build();
    }

    @Override
    @GET
    @Path("/monnit/sensors")
    public Response getAllSensors(@QueryParam("token") String token, @QueryParam("name") String name,
            @QueryParam("applicationID") String applicationId, @QueryParam("networkID") String networkID, @QueryParam("status") String status) {
        Map<String, String> paramMap = new HashedMap();
        if(name != null && !name.isEmpty()) {
            paramMap.put("name", name);
        }
        if(applicationId != null && !applicationId.isEmpty()) {
            paramMap.put("applicationID", applicationId);
        }
        if(networkID != null && !networkID.isEmpty()) {
            paramMap.put("networkID", networkID);
        }
        if(status != null && !status.isEmpty()) {
            paramMap.put("status", status);
        }
        String url  = TransportUtil.getURI(Constants.SENSOR_LIST, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);
        MonnitResponseUtil responseUtil = new MonnitResponseUtil();
        try {
            List<ApiSensor> apiSensors = (List<ApiSensor>) responseUtil.generateResultObj(responseObj);
            addSensorProperties(apiSensors);
        } catch (JAXBException e) {
            String msg = "Error occurred while unmarshalling document.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).entity(responseObj).build();
    }

    @Override
    @GET
    @Path("/monnit/gateways")
    public Response getAllGateways(@QueryParam("token") String token, @QueryParam("name") String name,
            @QueryParam("networkID") String networkID) {
        Map<String, String> paramMap = new HashedMap();
        if(name != null && !name.isEmpty()) {
            paramMap.put("name", name);
        }
        if(networkID != null && !networkID.isEmpty()) {
            paramMap.put("networkID", networkID);
        }
        String url  = TransportUtil.getURI(Constants.GATEWAY_LIST, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);
        MonnitResponseUtil responseUtil = new MonnitResponseUtil();
        try {
            List<ApiGateway> apiGateways = (List<ApiGateway>) responseUtil.generateResultObj(responseObj);
            addGatewayProperties(apiGateways);
        } catch (JAXBException e) {
            String msg = "Error occurred while unmarshalling document.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).entity(responseObj).build();
    }

    @Override
    @POST
    @Path("/monnit/sensors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignSensor(@QueryParam("token") String token, MonnitDevice device) {
        Map<String, String> paramMap = new HashedMap();
        paramMap.put(Constants.NETWORK_ID, device.getNetworkID());
        paramMap.put(Constants.SENSOR_ID, device.getSensorID());
        paramMap.put(Constants.CHECK_DIGIT, device.getCheckDigit());
        String url = TransportUtil.getURI(Constants.REG_SENSOR_EP, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);
        boolean response = false;
        if(responseObj != null && responseObj.getResult().equals("Success")) {
            paramMap.clear();
            paramMap.put(Constants.SENSOR_ID, device.getSensorID());
            url = TransportUtil.getURI(Constants.SENSOR_GET_EP, token, paramMap);
            responseObj = APIUtil.getResponse(url);
            MonnitResponseUtil responseUtil = new MonnitResponseUtil();
            try {
                response = registerDevice(device);
                List<ApiSensor> sensorList = new ArrayList<>();
                ApiSensor sensor = (ApiSensor)responseUtil.generateResultObj(responseObj);
                sensorList.add(sensor);
                addSensorProperties(sensorList);
            } catch (JAXBException e) {
                String msg = "Exception occurred when parsing xml document.";
                log.error(msg, e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
            }
        } else {
            String msg = "Exception occurred when registering device in iMonnit cloud.";
            log.error(msg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @POST
    @Path("/monnit/gateways")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignGateway(@QueryParam("token") String token, MonnitDevice device) {
        Map<String, String> paramMap = new HashedMap();
        paramMap.put(Constants.NETWORK_ID, device.getNetworkID());
        paramMap.put(Constants.GATEWAY_ID, device.getSensorID());
        paramMap.put(Constants.CHECK_DIGIT, device.getCheckDigit());
        String url = TransportUtil.getURI(Constants.REG_GATEWAY_EP, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);
        boolean response = false;
        if(responseObj != null && responseObj.getResult().equals("Success")) {
            paramMap.clear();
            paramMap.put(Constants.GATEWAY_ID, device.getGatewayID());
            url = TransportUtil.getURI(Constants.GATEWAY_GET_EP, token, paramMap);
            responseObj = APIUtil.getResponse(url);
            MonnitResponseUtil responseUtil = new MonnitResponseUtil();
            try {
                response = registerDevice(device);
                List<ApiGateway> gatewayList = new ArrayList<>();
                ApiGateway gateway = (ApiGateway)responseUtil.generateResultObj(responseObj);
                gatewayList.add(gateway);
                addGatewayProperties(gatewayList);
            } catch (JAXBException e) {
                String msg = "Exception occurred when parsing xml document.";
                log.error(msg, e);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
            }
        } else {
            String msg = "Exception occurred when registering device in iMonnit cloud.";
            log.error(msg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @GET
    @Path("/monnit/auth-token")
    public Response getAuthToken(@QueryParam("username") String username, @QueryParam("password") String password) {
        String token = APIUtil.getAuthToken(username, password);
        return Response.status(Response.Status.OK.getStatusCode()).entity(token).build();
    }

    @Override
    @POST
    @Path("/monnit/devices")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDeviceGroup(@QueryParam("gatewayID") int gatewayId, @QueryParam("deviceName") String deviceName, MonnitDeviceGroup grp) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(String.valueOf(gatewayId));
        deviceIdentifier.setType(Constants.DEVICE_TYPE);
        boolean response = false;
        try {
            if(!APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                MonnitDevice gatewayDevice = new MonnitDevice();
                gatewayDevice.setGatewayID(String.valueOf(gatewayId));
                gatewayDevice.setType("gateway");
                gatewayDevice.setName(deviceName);
                boolean status = registerDevice(gatewayDevice);
                if(!status) {
                    return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
                }
            }
            DeviceGroup group = new DeviceGroup();
            group.setName(deviceName);
            APIUtil.getGroupManagementService().createGroup(group, null, null);
            List<DeviceIdentifier> deviceList = new ArrayList<>();
            deviceList.add(deviceIdentifier);
            for (Integer id: grp.getSensorIds()) {
                deviceList.add(new DeviceIdentifier(String.valueOf(id), Constants.DEVICE_TYPE));
            }
            DeviceGroup curGroup = APIUtil.getGroupManagementService().getGroup(deviceName);
            APIUtil.getGroupManagementService().addDevices(curGroup.getGroupId(), deviceList);

            DeviceLocation location = grp.getLocation();
            location.setDeviceIdentifier(deviceIdentifier);
            APIUtil.updateDeviceLocation(location);
            response = true;
        } catch (DeviceManagementException e) {
            String msg = "Exception occurred when retrieving device in IoT server.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        } catch (GroupAlreadyExistException e) {
            String msg = "Group already exists.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        } catch (GroupManagementException e) {
            String msg = "Exception occurred when adding device group in IoT server.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        } catch (DeviceNotFoundException e) {
            String msg = "Exception occurred when retrieving devices.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        } catch (DeviceDetailsMgtException e) {
            String msg = "Exception occurred when retrieving device details.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @GET
    @Path("/monnit/devices")
    public Response getDeviceGroups(@QueryParam("gatewayID") int gatewayId, @QueryParam("deviceName") String deviceName) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(String.valueOf(gatewayId));
        deviceIdentifier.setType(Constants.DEVICE_TYPE);
        List<MonnitDeviceGroup> deviceGroups = new ArrayList<>();
        try {
            List<DeviceGroup> groups;
            if(deviceName.isEmpty()) {
                groups = APIUtil.getGroupManagementService().getGroups();
            } else {
                groups = new ArrayList<>();
                groups.add(APIUtil.getGroupManagementService().getGroup(deviceName));
            }
            for (DeviceGroup group : groups) {
                List<Device> devices = APIUtil.getGroupManagementService().getDevices(group.getGroupId(), -1, -1);
                MonnitDeviceGroup deviceGrp = new MonnitDeviceGroup();
                deviceGrp.setDeviceGroup(group);
                deviceGrp.setDevices(devices);
                deviceGroups.add(deviceGrp);
            }
        } catch (GroupManagementException e) {
            String msg = "Exception occurred when retrieving devices.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).entity(deviceGroups).build();
    }

    @Override
    @GET
    @Path("/monnit/webhook/create")
    public Response createWebHook(@QueryParam("token") String token, @QueryParam("baseUrl") String baseUrl) {
        Map<String, String> paramMap = new HashedMap();
        paramMap.put(Constants.BASE_URL, baseUrl);
        String url = TransportUtil.getURI(Constants.WEBHOOK_CREATE_EP, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);
        return null;
    }

    @Override
    @GET
    @Path("/monnit/recent-notifications")
    public Response getRecentNotifications(@QueryParam("minutes") String minutes, @QueryParam("lastNotificationID") String lastNotificationId,
            @QueryParam("sensorID") String sensorId) {
        return null;
    }

    @Override
    @GET
    @Path("/monnit/notifications")
    public Response getNotifications(@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("sensorID") String sensorID) {
        return null;
    }

    private boolean registerDevice(MonnitDevice monnitDevice) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        if(monnitDevice.getType().equals("sensor")) {
            deviceIdentifier.setId(monnitDevice.getSensorID());
        } else {
            deviceIdentifier.setId(monnitDevice.getGatewayID());
        }
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
            device.setName(monnitDevice.getName());
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

            if (monnitDevice.getType().equals("sensor")) {
                Device.Property sensorName = new Device.Property();
                sensorName.setName(Constants.SENSOR_NAME);
                Device.Property applicationId = new Device.Property();
                applicationId.setName(Constants.APPLICATION_ID);
                Device.Property csNetId = new Device.Property();
                csNetId.setName(Constants.CS_NET_ID);
                Device.Property currentReading = new Device.Property();
                currentReading.setName(Constants.CURRENT_READING);
                Device.Property batteryLevel = new Device.Property();
                batteryLevel.setName(Constants.BATTERY_LEVEL);
                Device.Property signalStrength = new Device.Property();
                signalStrength.setName(Constants.SIGNAL_STRENGTH);
                Device.Property alertsActive = new Device.Property();
                alertsActive.setName(Constants.ALERTS_ACTIVE);
                propertyList.add(sensorName);
                propertyList.add(applicationId);
                propertyList.add(csNetId);
                propertyList.add(currentReading);
                propertyList.add(batteryLevel);
                propertyList.add(signalStrength);
                propertyList.add(alertsActive);
            } else if (monnitDevice.getType().equals("gateway")) {
                Device.Property gatewayName = new Device.Property();
                gatewayName.setName(Constants.GATEWAY_NAME);
                Device.Property gatewayType = new Device.Property();
                gatewayType.setName(Constants.GATEWAY_TYPE);
                Device.Property heartBeat = new Device.Property();
                heartBeat.setName(Constants.HEART_BEAT);
                Device.Property isDirty = new Device.Property();
                isDirty.setName(Constants.IS_DIRTY);
                Device.Property lastComDate = new Device.Property();
                lastComDate.setName(Constants.LAST_COM_DATE);
                Device.Property lastInboundIp = new Device.Property();
                lastInboundIp.setName(Constants.LAST_INBOUND_IP);
                Device.Property mac = new Device.Property();
                mac.setName(Constants.MAC_ADDRESS);
                Device.Property isUnlocked = new Device.Property();
                isUnlocked.setName(Constants.IS_UNLOCKED);
                Device.Property accId = new Device.Property();
                accId.setName(Constants.ACCOUNT_ID);
                Device.Property signalStrength = new Device.Property();
                signalStrength.setName(Constants.SIGNAL_STRENGTH);
                Device.Property batteryLevel = new Device.Property();
                batteryLevel.setName(Constants.BATTERY_LEVEL);
                propertyList.add(gatewayName);
                propertyList.add(gatewayType);
                propertyList.add(heartBeat);
                propertyList.add(isDirty);
                propertyList.add(lastComDate);
                propertyList.add(lastInboundIp);
                propertyList.add(mac);
                propertyList.add(isUnlocked);
                propertyList.add(accId);
                propertyList.add(signalStrength);
                propertyList.add(batteryLevel);
            }

            propertyList.add(deviceType);
            propertyList.add(checkDigitProperty);
            device.setProperties(propertyList);

            boolean status = APIUtil.getDeviceManagementService().enrollDevice(device);
            if (status) {
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
    }

    private void addSensorProperties(List<ApiSensor> apiSensors) {
        try {
            for (ApiSensor sensor : apiSensors) {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
                deviceIdentifier.setId(sensor.getSensorId());
                deviceIdentifier.setType(Constants.DEVICE_TYPE);
                if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                    Device device = APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);
                    List<Device.Property> propertyList = device.getProperties();
                    for (Device.Property prop : propertyList) {
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
                    if (sensor.getStatus().equals("1")) {
                        status = EnrolmentInfo.Status.ACTIVE;
                    } else {
                        status = EnrolmentInfo.Status.INACTIVE;
                    }
                    APIUtil.getDeviceManagementService().updateDeviceEnrolmentInfo(device, status);
                }
            }
        } catch (DeviceManagementException e) {
            log.error("Exception occurred when retrieving device from IoT server.", e);
        }
    }

    private void addGatewayProperties(List<ApiGateway> apiGateways) {
        try {
            for (ApiGateway gateway : apiGateways) {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
                deviceIdentifier.setId(gateway.getGatewayId());
                deviceIdentifier.setType(Constants.DEVICE_TYPE);
                if (APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                    Device device = APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);
                    List<Device.Property> propertyList = device.getProperties();
                    for (Device.Property prop : propertyList) {
                        switch (prop.getName()) {
                        case Constants.GATEWAY_NAME:
                            prop.setValue(gateway.getName());
                            break;
                        case Constants.NETWORK_ID:
                            prop.setValue(gateway.getNetworkId());
                            break;
                        case Constants.GATEWAY_TYPE:
                            prop.setValue(gateway.getGatewayType());
                            break;
                        case Constants.HEART_BEAT:
                            prop.setValue(gateway.getHeartBeat());
                            break;
                        case Constants.IS_DIRTY:
                            prop.setValue(gateway.getIsDirty());
                            break;
                        case Constants.LAST_COM_DATE:
                            prop.setValue(gateway.getLastCommunicationDate());
                            break;
                        case Constants.LAST_INBOUND_IP:
                            prop.setValue(gateway.getLastInboundIp());
                            break;
                        case Constants.MAC_ADDRESS:
                            prop.setValue(gateway.getMac());
                            break;
                        case Constants.IS_UNLOCKED:
                            prop.setValue(gateway.getIsUnlocked());
                            break;
                        case Constants.CHECK_DIGIT:
                            prop.setValue(gateway.getCheckDigit());
                            break;
                        case Constants.ACCOUNT_ID:
                            prop.setValue(gateway.getAccId());
                            break;
                        case Constants.SIGNAL_STRENGTH:
                            prop.setValue(gateway.getSignalStrength());
                            break;
                        case Constants.BATTERY_LEVEL:
                            prop.setValue(gateway.getBatteryLevel());
                            break;
                        }
                    }
                    device.setProperties(propertyList);
                    EnrolmentInfo.Status status = EnrolmentInfo.Status.ACTIVE;
                    APIUtil.getDeviceManagementService().updateDeviceEnrolmentInfo(device, status);
                }
            }
        } catch (DeviceManagementException e) {
            log.error("Exception occurred when retrieving device from IoT server.", e);
        }
    }

}
