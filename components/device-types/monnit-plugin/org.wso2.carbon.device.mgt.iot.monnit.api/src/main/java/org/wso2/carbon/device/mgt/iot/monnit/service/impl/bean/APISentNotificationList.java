package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "APISentNotificationList")
public class APISentNotificationList {
    List<APISentNotification> notifications;

    @XmlElementWrapper
    @XmlElement(name = "APISentNotification")
    public List<APISentNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<APISentNotification> notifications) {
        this.notifications = notifications;
    }
}
