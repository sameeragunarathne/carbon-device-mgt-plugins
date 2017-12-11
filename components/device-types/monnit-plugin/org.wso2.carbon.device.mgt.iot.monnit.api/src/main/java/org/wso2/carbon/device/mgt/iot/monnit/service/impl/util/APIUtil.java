package org.wso2.carbon.device.mgt.iot.monnit.service.impl.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.api.AnalyticsDataAPI;
import org.wso2.carbon.analytics.api.AnalyticsDataAPIUtil;
import org.wso2.carbon.analytics.dataservice.commons.AnalyticsDataResponse;
import org.wso2.carbon.analytics.dataservice.commons.SearchResultEntry;
import org.wso2.carbon.analytics.dataservice.commons.SortByField;
import org.wso2.carbon.analytics.datasource.commons.Record;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.core.service.GroupManagementProviderService;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.AnalyticsResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.QueryPayload;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.SortBy;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants.Constants;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.dto.SensorRecord;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APIUtil {
    private static Log log = LogFactory.getLog(APIUtil.class);

    static {
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
        {
            public boolean verify(String hostname, SSLSession session)
            {
                // ip address of the service URL(like.23.28.244.244)
                if (hostname.equals("192.168.57.77") || hostname.equals("imonnit.com")) {
                    log.info(hostname);
                    return true;
                }
                return false;
            }
        });
    }

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

    public static NotificationManagementService getNotificationManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        NotificationManagementService notificationManagementService = (NotificationManagementService)
                ctx.getOSGiService(NotificationManagementService.class, null);
        if(notificationManagementService == null) {
            String msg = "Notification Management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return notificationManagementService;
    }

    public static GroupManagementProviderService getGroupManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        GroupManagementProviderService groupManagementProviderService =
                (GroupManagementProviderService) ctx.getOSGiService(GroupManagementProviderService.class, null);
        if(groupManagementProviderService == null) {
            String msg = "Group Management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return groupManagementProviderService;
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
        URLConnection httpsConnection;
        MonnitResponse responseObj = null;
        String response = "";
        int status;
        try {
            httpsConnection = TransportUtil.getConnection(url);
            if(httpsConnection instanceof HttpsURLConnection) {
                ((HttpsURLConnection)httpsConnection).setRequestMethod(Constants.HTTP_GET);
                httpsConnection.setRequestProperty("Accept", "text/xml");
                status = ((HttpsURLConnection)httpsConnection).getResponseCode();
            } else {
                ((HttpURLConnection)httpsConnection).setRequestMethod(Constants.HTTP_GET);
                httpsConnection.setRequestProperty("Accept", "text/xml");
                status = ((HttpURLConnection)httpsConnection).getResponseCode();
            }
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

    public static void deleteDeviceLocation(DeviceLocation deviceLocation) throws DeviceDetailsMgtException {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceInformationManager informationManager =
                (DeviceInformationManager) ctx.getOSGiService(DeviceInformationManager.class, null);
        informationManager.deleteDeviceLocation(deviceLocation);
    }

    public static AnalyticsDataAPI getAnalyticsDataAPI() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        AnalyticsDataAPI analyticsDataAPI =
                (AnalyticsDataAPI) ctx.getOSGiService(AnalyticsDataAPI.class, null);
        if (analyticsDataAPI == null) {
            String msg = "Analytics api service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return analyticsDataAPI;
    }

    public static List<SensorRecord> getAllEventsForSensorDevice(String tableName, String query,
            List<SortByField> sortByFields) throws AnalyticsException {
        int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
        AnalyticsDataAPI analyticsDataAPI = getAnalyticsDataAPI();
        int eventCount = analyticsDataAPI.searchCount(tenantId, tableName, query);
        if (eventCount == 0) {
            return null;
        }
        List<SearchResultEntry> resultEntries = analyticsDataAPI.search(tenantId, tableName, query, 0, eventCount,
                sortByFields);
        List<String> recordIds = getRecordIds(resultEntries);
        AnalyticsDataResponse response = analyticsDataAPI.get(tenantId, tableName, 1, null, recordIds);
        Map<String, SensorRecord> sensorDatas = createSensorData(AnalyticsDataAPIUtil.listRecords(
                analyticsDataAPI, response));
        List<SensorRecord> sortedSensorData = getSortedSensorData(sensorDatas, resultEntries);
        return sortedSensorData;
    }


    private static List<String> getRecordIds(List<SearchResultEntry> searchResults) {
        List<String> ids = new ArrayList<>();
        for (SearchResultEntry searchResult : searchResults) {
            ids.add(searchResult.getId());
        }
        return ids;
    }

    public static List<SensorRecord> getSortedSensorData(Map<String, SensorRecord> sensorDatas,
            List<SearchResultEntry> searchResults) {
        List<SensorRecord> sortedRecords = new ArrayList<>();
        for (SearchResultEntry searchResultEntry : searchResults) {
            sortedRecords.add(sensorDatas.get(searchResultEntry.getId()));
        }
        return sortedRecords;
    }

    public static Map<String, SensorRecord> createSensorData(List<Record> records) {
        Map<String, SensorRecord> sensorDatas = new HashMap<>();
        for (Record record : records) {
            SensorRecord sensorData = createSensorData(record);
            sensorDatas.put(sensorData.getId(), sensorData);
        }
        return sensorDatas;
    }

    public static SensorRecord createSensorData(Record record) {
        SensorRecord recordBean = new SensorRecord();
        recordBean.setId(record.getId());
        recordBean.setValues(record.getValues());
        return recordBean;
    }

    public static int sendAnalyticsData(String url, String payload) {
        HttpURLConnection httpsConnection;
        try {
            httpsConnection = (HttpURLConnection) TransportUtil.getConnection(url);
            httpsConnection.setRequestMethod(Constants.HTTP_POST);
            httpsConnection.setRequestProperty("Content-Type", "application/json");

            httpsConnection.setDoOutput(true);
            OutputStream os = httpsConnection.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            os.close();

            int status = httpsConnection.getResponseCode();
            return status;
        } catch (TransportHandlerException e) {
            log.error("Error occurred while establishing HTTP connection.", e);
        } catch (ProtocolException e) {
            log.error("Protocol specific error occurred when trying to set method to " + Constants.HTTP_POST, e);
        } catch (IOException e) {
            log.error("Error occurred while sending the request.", e);
        }
        return -1;
    }

    public static List<AnalyticsResponse> getAnalyticsData(String url, String tableName, String query, List<SortByField> sortByFields) {
        HttpURLConnection httpsConnection;
        AnalyticsResponse responseObj = null;
        String response = "";
        List<AnalyticsResponse> responseList = null;
        try {
            httpsConnection = (HttpURLConnection) TransportUtil.getConnection(url);
            httpsConnection.setRequestMethod(Constants.HTTP_POST);
            httpsConnection.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
            httpsConnection.setRequestProperty("Content-Type", "application/json");

            httpsConnection.setDoOutput(true);
            OutputStream os = httpsConnection.getOutputStream();
            QueryPayload payload = new QueryPayload();
            payload.setTableName(tableName);
            payload.setQuery(query);
            List<SortBy> sortByList = new ArrayList<>();
            for (SortByField field: sortByFields) {
                SortBy sortBy = new SortBy();
                sortBy.setField(field.getFieldName());
                sortBy.setSortType(field.getSortType());
                sortByList.add(sortBy);
            }
            payload.setSortBy(sortByList);
            payload.setStart(0);
            payload.setCount(10000);
            String payloadStr = new Gson().toJson(payload);
            os.write(payloadStr.getBytes());
            os.flush();
            os.close();

            int status = httpsConnection.getResponseCode();
            if(status == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpsConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response += inputLine;
                }
                in.close();
                responseList = new Gson().fromJson(response, new TypeToken<List<AnalyticsResponse>>(){}.getType());
            }
        } catch (ProtocolException e) {
            log.error("Protocol specific error occurred when trying to set method to " + Constants.HTTP_POST, e);
        } catch (TransportHandlerException e) {
            log.error("Error occurred while establishing HTTP connection.", e);
        } catch (IOException e) {
            log.error("Error occurred while sending the request.", e);
        }
        return responseList;
    }
}
