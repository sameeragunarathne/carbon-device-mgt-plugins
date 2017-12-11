package org.wso2.carbon.device.mgt.iot.monnit.service.impl.constants;

public class Constants {
//    public static final String SERVER_PROTOCOL = "https";
//    public static final String HOST = "https://www.imonnit.com/";
    public static final String SERVER_PROTOCOL = "http";
    public static final String HOST = "http://localhost:8080/";
    public static final String TOKEN_EP = HOST + "xml/GetAuthToken/?";
    public static final String SENSOR_LIST = HOST + "xml/SensorList/";
    public static final String REG_SENSOR_EP = HOST + "xml/AssignSensor/";
    public static final String SENSOR_GET_EP = HOST + "xml/SensorGet/";
    public static final String GATEWAY_LIST = HOST + "xml/GatewayList/";
    public static final String REG_GATEWAY_EP = HOST + "xml/AssignGateway/";
    public static final String GATEWAY_GET_EP = HOST + "xml/GatewayGet/";
    public static final String WEBHOOK_CREATE_EP = HOST + "xml/WebHookCreate/";
    public static final String NOTIFICATION_EP = HOST + "xml/RecentlySentNotifications/";

    public static final String HTTP_POST = "POST";
    public static final String HTTP_GET = "GET";

    public static final String DEVICE_TYPE = "monnit";

    public static final String PERM_ENROLL_MONNIT = "/permission/admin/device-mgt/devices/enroll/monnit";
    public static final String PERM_OWNING_DEVICE_VIEW = "/permission/admin/device-mgt/devices/owning-device/view";

    public static final String ROLE_NAME = "internal/devicemgt-user";

    //sensor properties
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
    public static final String IS_ASSIGNED = "IsAssigned";

    //gateway properties
    public static final String GATEWAY_ID = "gatewayID";
    public static final String GATEWAY_NAME = "Name";
    public static final String GATEWAY_TYPE = "GatewayType";
    public static final String HEART_BEAT = "Heartbeat";
    public static final String IS_DIRTY = "IsDirty";
    public static final String LAST_COM_DATE = "LastCommunicationDate";
    public static final String LAST_INBOUND_IP = "LastInboundIPAddress";
    public static final String MAC_ADDRESS = "MacAddress";
    public static final String IS_UNLOCKED = "IsUnlocked";
    public static final String CHK_DIGIT = "CheckDigit";
    public static final String ACCOUNT_ID = "AccountID";

    public static final String MINUTES = "minutes";
    public static final String LAST_SENT_NOTIFICATION_ID = "lastSentNotificationID";

    public static final String BASE_URL = "baseUrl";
    public static final String SERVER_HOST = "localhost";
    public static final String ANALYTICS_PORT = "9765";

    public static final String EVENT_TABLE = "ORG_WSO2_MONNIT_SENSORSTREAM" ;
    public static final String HUMIDITY_EVENT_TABLE = "MONNIT_DEVICE_STREAM_SENSOR_HUMIDITY" ;
    public static final String METADATA_EVENT_TABLE = "MONNIT_DEVICE_STREAM_SENSOR_METADATA" ;

}
