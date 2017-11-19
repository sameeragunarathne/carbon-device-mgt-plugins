package org.wso2.carbon.device.mgt.iot.monnit.service.impl.util;

import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensor;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensorList;
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
import java.util.List;
import java.util.Map;

public class APIUtil {
    private static Log log = LogFactory.getLog(APIUtil.class);

    //TODO get token from the ui web app
    public static String getAuthToken() {

//        HttpsURLConnection httpsConnection;
        HttpURLConnection httpsConnection;
        MonnitResponse responseObj;
        String response = "";
        try {
            Map<String, String> credMap = new HashedMap();
            credMap.put("username", "sameera.lakruwan");
            credMap.put("password", "Sniper123");
            String url  = TransportUtil.getURI(Constants.TOKEN_EP, credMap);
            httpsConnection = TransportUtil.getHttpConnection(url);
            httpsConnection.setRequestMethod(Constants.HTTP_GET);
            httpsConnection.setRequestProperty("Accept", "text/xml");

            int status = httpsConnection.getResponseCode();
//            log.info(status);
            if(status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpsConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response += inputLine;
                }
                in.close();
                responseObj = generateResponseObj(response);
                response = (String) responseObj.getResult();
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
        return response;
    }

    public static String getMonnitResponse(String token) {
//        HttpsURLConnection httpsConnection;
        HttpURLConnection httpsConnection;
        MonnitResponse responseObj;
        String response = "";
        try {
            Map<String, String> credMap = new HashedMap();
            String url  = TransportUtil.getURI(Constants.SENSOR_LIST, token, credMap);
            log.info(url);
            httpsConnection = TransportUtil.getHttpConnection(url);
            httpsConnection.setRequestMethod(Constants.HTTP_GET);
            httpsConnection.setRequestProperty("Accept", "text/xml");

            int status = httpsConnection.getResponseCode();
            if(status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        httpsConnection.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response += inputLine;
                }
                responseObj = generateResponseObj(response);
                generateResultObj(responseObj);
//                response = responseObj.getResult();
                in.close();
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
        return response;
    }

    private static MonnitResponse generateResponseObj(String resp) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(MonnitResponse.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        StringReader reader = new StringReader(resp);
        MonnitResponse respObj = (MonnitResponse) unmarshaller.unmarshal(reader);
        return respObj;
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
}
