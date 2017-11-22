package org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants;

public class Constants {
    public static final String HOST = "http://localhost:8080/";
    public static final String TOKEN_EP = HOST + "xml/GetAuthToken/?";
    public static final String SENSOR_LIST = HOST + "xml/SensorList/";
    public static final String REG_SENSOR_EP = HOST + "xml/AssignSensor/";
    public static final String SENSOR_GET_EP = HOST + "xml/SensorGet/";
    public static final String GATEWAY_LIST = HOST + "xml/GatewayList/";
    public static final String REG_GATEWAY_EP = HOST + "xml/AssignGateway/";

    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";

    public static final String DEVICE_TYPE = "monnit";

    public static final String PERM_ENROLL_MONNIT = "/permission/admin/device-mgt/devices/enroll/monnit";
    public static final String PERM_OWNING_DEVICE_VIEW = "/permission/admin/device-mgt/devices/owning-device/view";

    public static final String ROLE_NAME = "internal/devicemgt-user";

    public static final String SENSOR_ID = "sensorID";
    public static final String NETWORK_ID = "networkID";
    public static final String CHECK_DIGIT = "checkDigit";
    public static final String TYPE = "type";
    public static final String SENSOR_NAME = "SensorName";
    public static final String APPLICATION_ID = "MonnitApplicationID";
    public static final String CS_NET_ID = "CSNetID";
    public static final String CURRENT_READING = "CurrentReading";
    public static final String BATTERY_LEVEL = "BatteryLevel";
    public static final String SIGNAL_STRENGTH = "SignalStrength";
    public static final String ALERTS_ACTIVE = "AlertsActive";
}
