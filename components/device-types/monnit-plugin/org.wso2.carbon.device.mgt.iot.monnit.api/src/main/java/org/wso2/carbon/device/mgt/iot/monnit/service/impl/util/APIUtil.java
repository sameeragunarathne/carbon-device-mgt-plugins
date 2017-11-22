package org.wso2.carbon.device.mgt.iot.monnit.service.impl.util;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;
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
        Map<String, String> credMap = new HashedMap();
        credMap.put("username", username);
        credMap.put("password", password);
        String url  = TransportUtil.getURI(Constants.TOKEN_EP, credMap);
        MonnitResponse responseObj = getResponse(url);
        return (String) responseObj.getResult();
    }

    public static String getGatewayList(String token, String name) {
        String response = "";
        Map<String, String> paramMap = new HashedMap();
        paramMap.put("name", name);
        String url  = TransportUtil.getURI(Constants.GATEWAY_LIST, token, paramMap);
        MonnitResponse responseObj = getResponse(url);
        return response;
    }

    public static MonnitResponse getResponse(String url) {
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

    public static void updateDeviceLocation(DeviceLocation deviceLocation) throws DeviceDetailsMgtException {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceInformationManager informationManager =
                (DeviceInformationManager) ctx.getOSGiService(DeviceInformationManager.class, null);
        informationManager.addDeviceLocation(deviceLocation);
    }

}
