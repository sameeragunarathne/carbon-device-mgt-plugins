package org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants;

public class Constants {
    public static final String HOST = "http://localhost:8080/";
    public static final String TOKEN_EP = HOST + "xml/GetAuthToken/?";
    public static final String SENSOR_LIST = HOST + "xml/SensorList/";
    public static final String REG_SENSOR_EP = HOST + "xml/AssignSensor/";
    public  static final String SENSOR_GET_EP = HOST + "xml/SensorGet/";

    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";

    public static final String DEVICE_TYPE = "monnit";
}
