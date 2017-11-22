package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "APISensor")
public class ApiSensor {

    private String sensorId;
    private String applicationId;
    private String csNetId;
    private String sensorName;
    private String lastCommunicationDate;
    private String nextCommunicationDate;
    private String lastDataMsgId;
    private String powerSourceId;
    private String status;
    private String canUpdate;
    private String currentReading;
    private String batteryLevel;
    private String signalStrength;
    private String alertsActive;
    private String checkDigit;

    @XmlAttribute(name = "SensorID")
    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    @XmlAttribute(name = "MonnitApplicationID")
    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @XmlAttribute(name = "CSNetID")
    public String getCsNetId() {
        return csNetId;
    }

    public void setCsNetId(String csNetId) {
        this.csNetId = csNetId;
    }

    @XmlAttribute(name = "SensorName")
    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    @XmlAttribute(name = "LastCommunicationDate")
    public String getLastCommunicationDate() {
        return lastCommunicationDate;
    }

    public void setLastCommunicationDate(String lastCommunicationDate) {
        this.lastCommunicationDate = lastCommunicationDate;
    }

    @XmlAttribute(name = "NextCommunicationDate")
    public String getNextCommunicationDate() {
        return nextCommunicationDate;
    }

    public void setNextCommunicationDate(String nextCommunicationDate) {
        this.nextCommunicationDate = nextCommunicationDate;
    }

    @XmlAttribute(name = "LastDataMessageID")
    public String getLastDataMsgId() {
        return lastDataMsgId;
    }

    public void setLastDataMsgId(String lastDataMsgId) {
        this.lastDataMsgId = lastDataMsgId;
    }

    @XmlAttribute(name = "PowerSourceID")
    public String getPowerSourceId() {
        return powerSourceId;
    }

    public void setPowerSourceId(String powerSourceId) {
        this.powerSourceId = powerSourceId;
    }

    @XmlAttribute(name = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @XmlAttribute(name = "CanUpdate")
    public String isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(String canUpdate) {
        this.canUpdate = canUpdate;
    }

    @XmlAttribute(name = "CurrentReading")
    public String getCurrentReading() {
        return currentReading;
    }

    public void setCurrentReading(String currentReading) {
        this.currentReading = currentReading;
    }

    @XmlAttribute(name = "BatteryLevel")
    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    @XmlAttribute(name = "SignalStrength")
    public String getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }

    @XmlAttribute(name = "AlertsActive")
    public String isAlertsActive() {
        return alertsActive;
    }

    public void setAlertsActive(String alertsActive) {
        this.alertsActive = alertsActive;
    }

    @XmlAttribute(name = "CheckDigit")
    public String getCheckDigit() {
        return checkDigit;
    }

    public void setCheckDigit(String checkDigit) {
        this.checkDigit = checkDigit;
    }
}
