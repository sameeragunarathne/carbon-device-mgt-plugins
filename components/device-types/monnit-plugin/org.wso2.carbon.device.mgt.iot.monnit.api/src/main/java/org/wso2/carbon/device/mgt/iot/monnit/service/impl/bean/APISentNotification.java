package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "APISentNotification")
public class APISentNotification {

    private String sentNotificationID;
    private String notificationID;
    private String userID;
    private String sensorID;
    private String gatewayID;
    private String text;
    private String content;
    private String notificationDate;

    @XmlAttribute(name = "SentNotificationID")
    public String getSentNotificationID() {
        return sentNotificationID;
    }

    public void setSentNotificationID(String sentNotificationID) {
        this.sentNotificationID = sentNotificationID;
    }

    @XmlAttribute(name = "NotificationID")
    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    @XmlAttribute(name = "UserID")
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @XmlAttribute(name = "SensorID")
    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    @XmlAttribute(name = "GatewayID")
    public String getGatewayID() {
        return gatewayID;
    }

    public void setGatewayID(String gatewayID) {
        this.gatewayID = gatewayID;
    }

    @XmlAttribute(name = "Text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @XmlAttribute(name = "Content")
    public String getContent() {
        return content;
    }

    @XmlAttribute(name = "NotificationDate")
    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
