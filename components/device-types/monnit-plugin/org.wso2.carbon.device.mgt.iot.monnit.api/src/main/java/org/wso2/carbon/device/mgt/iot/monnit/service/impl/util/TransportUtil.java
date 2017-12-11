package org.wso2.carbon.device.mgt.iot.monnit.service.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants.Constants;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class TransportUtil {

    private static final Log log = LogFactory.getLog(TransportUtil.class);

//    public static HttpsURLConnection getConnection(String urlString) throws
//            TransportHandlerException {
//        URL connectionUrl;
//        HttpsURLConnection httpsConnection;
//
//        try {
//            connectionUrl = new URL(urlString);
//            httpsConnection = (HttpsURLConnection) connectionUrl.openConnection();
//        } catch (MalformedURLException e) {
//            String errorMsg = "Error occured whilst trying to form HTTP-URL from string: " + urlString;
//            log.error(errorMsg);
//            throw new TransportHandlerException(errorMsg, e);
//        } catch (IOException exception) {
//            String errorMsg = "Error occured whilst trying to open a connection to: " + urlString;
//            log.error(errorMsg);
//            throw new TransportHandlerException(errorMsg, exception);
//        }
//        return httpsConnection;
//    }

    public static URLConnection getConnection(String urlString) throws
            TransportHandlerException {
        URL connectionUrl;
        URLConnection connection;

        try {
            connectionUrl = new URL(urlString);
            if("https".equals(Constants.SERVER_PROTOCOL)) {
                connection = (HttpsURLConnection) connectionUrl.openConnection();
            } else {
                connection = (HttpURLConnection) connectionUrl.openConnection();
            }
        } catch (MalformedURLException e) {
            String errorMsg = "Error occured whilst trying to form HTTP-URL from string: " + urlString;
            log.error(errorMsg);
            throw new TransportHandlerException(errorMsg, e);
        } catch (IOException exception) {
            String errorMsg = "Error occured whilst trying to open a connection to: " + urlString;
            log.error(errorMsg);
            throw new TransportHandlerException(errorMsg, exception);
        }
        return connection;
    }

    public static String getURI(String url, Map<String, String> params) {
        for (Map.Entry<String,String> entry: params.entrySet()) {
            url += entry.getKey() +"="+entry.getValue()+"&";
        }
        return url.substring(0, url.length()-1);
    }

    public static String getURI(String url, String token, Map<String, String> params) {
        url += token + "/?";
        for (Map.Entry<String,String> entry: params.entrySet()) {
            url += entry.getKey() +"="+entry.getValue()+"&";
        }
        return url.substring(0, url.length()-1);
    }
}