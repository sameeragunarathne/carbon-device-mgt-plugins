package org.wso2.carbon.device.mgt.iot.monnit.service.impl.util;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensor;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.Result;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants.Constants;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class APIUtil {
    private static Log log = LogFactory.getLog(APIUtil.class);

    //carbon user service

    public static String getAuthenticatedUser() {
        PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        String username = threadLocalCarbonContext.getUsername();
        String tenantDomain = threadLocalCarbonContext.getTenantDomain();
        if (username.endsWith(tenantDomain)) {
            return username.substring(0, username.lastIndexOf("@"));
        }
        return username;
    }

    public static DeviceManagementProviderService getDeviceManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceManagementProviderService deviceManagementProviderService =
                (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        if (deviceManagementProviderService == null) {
            String msg = "Device Management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceManagementProviderService;
    }

    //TODO get token from the ui web app
    public static String getAuthToken(String username, String password) {
        String response = "";
        Map<String, String> credMap = new HashedMap();
        credMap.put("username", username);
        credMap.put("password", password);
        String url  = TransportUtil.getURI(Constants.TOKEN_EP, credMap);
        MonnitResponse responseObj = getResponse(url);
        return response;
    }

    public static String getSensorsList(String token, String name, String applicationId) {
        String response = "";
        Map<String, String> paramMap = new HashedMap();
        paramMap.put("name", name);
        paramMap.put("applicationID", applicationId);
        String url  = TransportUtil.getURI(Constants.SENSOR_LIST, token, paramMap);
        MonnitResponse responseObj = getResponse(url);
        return response;
    }

    public static boolean registerDevice(String token, String networkId, String sensorId, String checkDigit) {
        Map<String, String> paramMap = new HashedMap();
        paramMap.put(Constants.NETWORK_ID, networkId);
        paramMap.put(Constants.SENSOR_ID, sensorId);
        paramMap.put(Constants.CHECK_DIGIT, checkDigit);
        String url = TransportUtil.getURI(Constants.REG_SENSOR_EP, token, paramMap);
        MonnitResponse responseObj = getResponse(url);

        if(responseObj != null && responseObj.getResult().equals("Success")) {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(sensorId);
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
                device.setName(checkDigit);
                device.setType(Constants.DEVICE_TYPE);
                enrolmentInfo.setOwner(APIUtil.getAuthenticatedUser());
                device.setEnrolmentInfo(enrolmentInfo);
                Device.Property checkDigitProperty = new Device.Property();
                checkDigitProperty.setName(Constants.CHECK_DIGIT);
                checkDigitProperty.setValue(checkDigit);

                List<Device.Property> propertyList = new ArrayList<>();
                propertyList.add(checkDigitProperty);
                device.setProperties(propertyList);

                return APIUtil.getDeviceManagementService().enrollDevice(device);
            } catch (DeviceManagementException e) {
                log.error("Exception occurred when registering device in IoT server", e);
                return false;
            }
        } else {
            log.error("Exception occurred when registering device in iMonnit cloud");
            return false;
        }
    }

    private static MonnitResponse generateResultObj(MonnitResponse resp) throws JAXBException {
        ElementNSImpl payload = ((ElementNSImpl) resp.getResult());
        if(payload.getFirstChild().getNodeName().equals("APISensorList")) {
            JAXBContext payloadContext = JAXBContext.newInstance(Result.class);
            Result list = (Result)payloadContext.createUnmarshaller().unmarshal(payload);
            List<ApiSensor> apiSensors = list.getApiSensors();
            for (ApiSensor sensor: apiSensors) {
                log.info(sensor.getSensorName());
            }
        }
        return null;
    }

    private static MonnitResponse getResponse(String url) {
        HttpURLConnection httpsConnection;
        MonnitResponse responseObj = null;
        String response = "";
        try {
            httpsConnection = TransportUtil.getHttpConnection(url);
            httpsConnection.setRequestMethod(Constants.HTTP_GET);
            httpsConnection.setRequestProperty("Accept", "text/xml");

            int status = httpsConnection.getResponseCode();
            if(status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpsConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response += inputLine;
                }
                in.close();
                JAXBContext ctx = JAXBContext.newInstance(MonnitResponse.class);
                Unmarshaller unmarshaller = ctx.createUnmarshaller();
                StringReader reader = new StringReader(response);
                responseObj = (MonnitResponse) unmarshaller.unmarshal(reader);
            }
        } catch (TransportHandlerException e) {
            log.error("Error occurred while establishing HTTP connection.", e);
        } catch (ProtocolException e) {
            log.error("Protocol specific error occurred when trying to set method to " + Constants.HTTP_GET, e);
        } catch (IOException e) {
            log.error("Error occurred while sending the request.", e);
        } catch (JAXBException e) {
            log.error("Error occurred while parsing xml response.", e);
        }
        return  responseObj;
    }

}
