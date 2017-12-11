package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.dataservice.commons.SortByField;
import org.wso2.carbon.analytics.dataservice.commons.SortType;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceNotFoundException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupAlreadyExistException;
import org.wso2.carbon.device.mgt.common.group.mgt.GroupManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.APISentNotification;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.AnalyticsResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiGateway;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensor;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitDevice;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitDeviceGroup;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.SensorMessage;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.SensorPayload;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants.Constants;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.dto.SensorRecord;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.Collections;
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
                addSensorProperties(sensor);
            }

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
                addGatewayProperties(gateway);
            }
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
            for (ApiSensor sensor : apiSensors) {
                MonnitDevice device = new MonnitDevice();
                device.setSensorID(sensor.getSensorId());
                device.setType("sensor");
                device.setCheckDigit(sensor.getCheckDigit());
                registerDevice(device);
                addSensorProperties(sensor);
                if(isAssigned(sensor.getSensorId())) {
                    apiSensors.remove(sensor);
                }
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(ApiSensor.class);
            String response = responseUtil.marshalObjToStr(jaxbContext, apiSensors);
            return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
        } catch (JAXBException e) {
            String msg = "Error occurred while unmarshalling document.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
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
            for (ApiGateway gateway : apiGateways) {
                MonnitDevice device = new MonnitDevice();
                device.setGatewayID(gateway.getGatewayId());
                device.setType("gateway");
                device.setGatewayID(gateway.getCheckDigit());
                registerDevice(device);
                addGatewayProperties(gateway);
                if(isAssigned(gateway.getGatewayId())) {
                    apiGateways.remove(gateway);
                }
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(ApiGateway.class);
            String response = responseUtil.marshalObjToStr(jaxbContext, apiGateways);
            return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
        } catch (JAXBException e) {
            String msg = "Error occurred while unmarshalling document.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
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
                ApiSensor sensor = (ApiSensor)responseUtil.generateResultObj(responseObj);
                addSensorProperties(sensor);
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
                ApiGateway gateway = (ApiGateway)responseUtil.generateResultObj(responseObj);
                addGatewayProperties(gateway);
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
    public Response addDeviceGroup(MonnitDeviceGroup grp) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(String.valueOf(grp.getGatewayID()));
        deviceIdentifier.setType(Constants.DEVICE_TYPE);
        boolean response = false;
        try {
            if(!APIUtil.getDeviceManagementService().isEnrolled(deviceIdentifier)) {
                MonnitDevice gatewayDevice = new MonnitDevice();
                gatewayDevice.setGatewayID(String.valueOf(grp.getGatewayID()));
                gatewayDevice.setType("gateway");
                gatewayDevice.setName(grp.getDeviceName());
                boolean status = registerDevice(gatewayDevice);
                if(!status) {
                    return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
                }
            }
            DeviceGroup group = new DeviceGroup();
            group.setName(grp.getDeviceName());
            APIUtil.getGroupManagementService().createGroup(group, null, null);
            List<DeviceIdentifier> deviceList = new ArrayList<>();
            deviceList.add(deviceIdentifier);
            for (Integer id: grp.getSensorIds()) {
                deviceList.add(new DeviceIdentifier(String.valueOf(id), Constants.DEVICE_TYPE));
            }
            DeviceGroup curGroup = APIUtil.getGroupManagementService().getGroup(grp.getDeviceName());
            APIUtil.getGroupManagementService().addDevices(curGroup.getGroupId(), deviceList);
            addSensorAssignedGateway(deviceList, String.valueOf(grp.getGatewayID()));

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
                List<Device> devices = APIUtil.getGroupManagementService().getDevices(group.getGroupId(), 0, 10000);
                List<Device> grpDevices = new ArrayList<>();
                for (Device device : devices) {
                    DeviceIdentifier identifier = new DeviceIdentifier(device.getDeviceIdentifier(), Constants.DEVICE_TYPE);
                    grpDevices.add(APIUtil.getDeviceManagementService().getDevice(identifier));
                }
                MonnitDeviceGroup deviceGrp = new MonnitDeviceGroup();
                deviceGrp.setDeviceGroup(group);
                deviceGrp.setDevices(grpDevices);
                deviceGrp.setDeviceName(group.getName());
                deviceGroups.add(deviceGrp);
            }
        } catch (GroupManagementException e) {
            String msg = "Exception occurred when retrieving devices.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        } catch (DeviceManagementException e) {
            String msg = "Exception occurred when retrieving devices.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        String responseJson = new Gson().toJson(deviceGroups);
        return Response.status(Response.Status.OK.getStatusCode()).entity(responseJson).build();
    }

    @Override
    @GET
    @Path("/monnit/devices/group")
    public Response getDeviceGroupName(@QueryParam("gatewayID") int gatewayId, @QueryParam("deviceName") String deviceName) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(String.valueOf(gatewayId));
        deviceIdentifier.setType(Constants.DEVICE_TYPE);
        try {
            List<DeviceGroup> list = APIUtil.getGroupManagementService().getGroups(deviceIdentifier);
            String response = list.get(1).getName();
            return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
        } catch (GroupManagementException e) {
            String msg = "Exception occurred when retrieving devices.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
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
    public Response getRecentNotifications(@QueryParam("token") String token, @QueryParam("minutes") String minutes, @QueryParam("lastNotificationID") String lastNotificationId,
            @QueryParam("sensorID") String sensorId) {
        Map<String, String> paramMap = new HashedMap();
        paramMap.put(Constants.MINUTES, minutes);
        if(lastNotificationId != null && !lastNotificationId.isEmpty()) {
            paramMap.put(Constants.LAST_SENT_NOTIFICATION_ID, lastNotificationId);
        }
        if(sensorId != null && !sensorId.isEmpty()) {
            paramMap.put(Constants.SENSOR_ID, sensorId);
        }
        String url = TransportUtil.getURI(Constants.NOTIFICATION_EP, token, paramMap);
        MonnitResponse responseObj = APIUtil.getResponse(url);
        if(responseObj == null) {
            String msg = "no notifications";
            return Response.status(Response.Status.OK.getStatusCode()).entity(msg).build();
        }
        MonnitResponseUtil responseUtil = new MonnitResponseUtil();
        try {
            List<APISentNotification> notifications = (List<APISentNotification>) responseUtil.generateResultObj(responseObj);
            for (APISentNotification notification : notifications) {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
                deviceIdentifier.setId(sensorId);
                deviceIdentifier.setType(Constants.DEVICE_TYPE);
                Notification deviceNotification = new Notification();
                deviceNotification.setDescription(notification.getText());
                deviceNotification.setDeviceType(Constants.DEVICE_TYPE);
                deviceNotification.setDeviceIdentifier(sensorId);
                deviceNotification.setStatus(Notification.Status.NEW.toString());
                //TODO map with device mgt operations
//                APIUtil.getNotificationManagementService().addNotification(deviceIdentifier, deviceNotification);
            }
            String notificationStr = new Gson().toJson(notifications);
            return Response.status(Response.Status.OK.getStatusCode()).entity(notificationStr).build();
        } catch (JAXBException e) {
            String msg = "Error occurred while unmarshalling document.";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
//        catch (NotificationManagementException e) {
//            String msg = "Error occurred while adding notification.";
//            log.error(msg, e);
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
//        }
    }

    @Override
    @GET
    @Path("/monnit/notifications")
    public Response getNotifications(@QueryParam("token") String token, @QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("sensorID") String sensorID) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(sensorID);
        deviceIdentifier.setType(Constants.DEVICE_TYPE);
        DeviceLocation location = new DeviceLocation();
        location.setDeviceIdentifier(deviceIdentifier);
        try {
            APIUtil.getDeviceManagementService().getDevice(deviceIdentifier);
            APIUtil.updateDeviceLocation(location);
        } catch (DeviceDetailsMgtException e) {
            String msg = "Exception occurred while updating location";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        } catch (DeviceManagementException e) {
            String msg = "Exception occurred while disenrolling device";
            log.error(msg, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(msg).build();
        }
        return Response.status(Response.Status.OK.getStatusCode()).build();
    }

    @Override
    @Path("/monnit/sensor/stats")
    @GET
    public Response getSensorStats(@QueryParam("deviceId") String deviceId, @QueryParam("from") long from, @QueryParam("to") long to,
            @QueryParam("applicationId") String applicationId, @QueryParam("gatewayId") String gatewayId) {
        String query = "";
        String fromDate = String.valueOf(from);
        String toDate = String.valueOf(to);
        if(applicationId != null && !applicationId.isEmpty()) {
            query = "gatewayID:" + gatewayId +" AND applicationID:" + applicationId + " AND messageDate : [" + fromDate + " TO " + toDate + "]";
        } else if(gatewayId != null && !gatewayId.isEmpty()) {
            query = "gatewayID:" + gatewayId + " AND messageDate : [" + fromDate + " TO " + toDate + "]";
        } else if(deviceId != null && !deviceId.isEmpty()) {
            query = "sensorID:" + deviceId + " AND messageDate : [" + fromDate + " TO " + toDate + "]";
        }
        String sensorTableName = Constants.EVENT_TABLE;
        try {
            List<SortByField> sortByFields = new ArrayList<>();
            SortByField sortByField = new SortByField("messageDate", SortType.ASC);
            sortByFields.add(sortByField);
            List<SensorRecord> sensorRecords = null;
            sensorRecords = APIUtil.getAllEventsForSensorDevice(sensorTableName, query, sortByFields);
//        String analyticsUrl = "https://192.168.57.77:9445/analytics/search";
//        log.info(analyticsUrl);
//            List<AnalyticsResponse> responseList = APIUtil.getAnalyticsData(analyticsUrl, sensorTableName, query, sortByFields);
//            responseList.removeAll(Collections.singleton(null));
            String records = new Gson().toJson(sensorRecords);
            return Response.status(Response.Status.OK.getStatusCode()).entity(records).build();
        }
        catch (AnalyticsException e) {
            String errorMsg = "Error on retrieving stats on table " + sensorTableName + " with query " + query;
            log.error(errorMsg);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()).entity(errorMsg).build();
        }
    }

    @Override
    @Path("/monnit/sensor/stats")
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response publishSensorData(SensorPayload payload) {
        if(payload != null) {
            String url = "http://localhost:9765/endpoints/Floor-Analysis-Http-Receiver";
            for (SensorMessage msg : payload.getSensorMessages()) {
                int status = APIUtil.sendAnalyticsData(url, new Gson().toJson(msg));
                if(status == -1) {
                    log.error("Exception occurred when publishing analytics data to IoT server.");
                }
            }
        }
        return Response.status(Response.Status.OK.getStatusCode()).build();
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
            Device.Property gateway = new Device.Property();
            gateway.setName(Constants.IS_ASSIGNED);
            propertyList.add(gateway);

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
            DeviceLocation location = monnitDevice.getLocation();
            if (status && location != null) {
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

    private void addSensorProperties(ApiSensor sensor) {
        try {
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
                //                    DeviceInfo info = new DeviceInfo();
                //                    info.setDeviceDetailsMap();
                if (sensor.getStatus().equals("1")) {
                    status = EnrolmentInfo.Status.ACTIVE;
                } else {

                    status = EnrolmentInfo.Status.INACTIVE;
                }
                EnrolmentInfo info = device.getEnrolmentInfo();
                info.setStatus(status);
                device.setEnrolmentInfo(info);
                APIUtil.getDeviceManagementService().updateDeviceInfo(deviceIdentifier, device);
            }
        } catch (DeviceManagementException e) {
            log.error("Exception occurred when retrieving device from IoT server.", e);
        }
    }

    private void addSensorAssignedGateway(List<DeviceIdentifier> deviceIds, String gatewayId) {
        try {
            for (DeviceIdentifier id : deviceIds) {
                if (APIUtil.getDeviceManagementService().isEnrolled(id)) {
                    List<Device.Property> propertyList = APIUtil.getDeviceManagementService().getDevice(id).getProperties();
                    for (Device.Property prop : propertyList) {
                        if(prop.getName().equals(Constants.IS_ASSIGNED)) {
                            prop.setValue(gatewayId);
                        }
                    }
                }
            }
        } catch (DeviceManagementException e) {
            log.error("Exception occurred when retrieving device from IoT server.", e);
        }
    }

    private void addGatewayProperties(ApiGateway gateway) {
        try {
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
                EnrolmentInfo info = device.getEnrolmentInfo();
                info.setStatus(status);
                device.setEnrolmentInfo(info);
                APIUtil.getDeviceManagementService().updateDeviceInfo(deviceIdentifier, device);
            }
        } catch (DeviceManagementException e) {
            log.error("Exception occurred when retrieving device from IoT server.", e);
        }
    }

    private boolean isAssigned(String id) {
        DeviceIdentifier identifier = new DeviceIdentifier(id ,Constants.DEVICE_TYPE);
        try {
            Device device = APIUtil.getDeviceManagementService().getDevice(identifier);
            for (Device.Property prop : device.getProperties()) {
                if(prop.getName().equals(Constants.IS_ASSIGNED) && prop.getValue() != null) {
                    return true;
                }
            }
        } catch (DeviceManagementException e) {
            log.error("Exception occurred when retrieving device from IoT server.", e);
        }
        return false;
    }

}
