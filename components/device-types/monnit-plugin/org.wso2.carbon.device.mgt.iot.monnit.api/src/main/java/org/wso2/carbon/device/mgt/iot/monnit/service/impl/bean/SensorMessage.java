package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

public class SensorMessage {
    private String sensorID;
    private String sensorName;
    private String applicationID;
    private String networkID;
    private String dataMessageGUID;
    private String state;
    private String messageDate;
    private String rawData;
    private String dataType;
    private String dataValue;
    private String plotValues;
    private String plotLabels;
    private String batteryLevel;
    private String signalStrength;
    private String pendingChange;

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(String applicationID) {
        this.applicationID = applicationID;
    }

    public String getNetworkID() {
        return networkID;
    }

    public void setNetworkID(String networkID) {
        this.networkID = networkID;
    }

    public String getDataMessageGUID() {
        return dataMessageGUID;
    }

    public void setDataMessageGUID(String dataMessageGUID) {
        this.dataMessageGUID = dataMessageGUID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMessageDate() {
        return messageDate;
    }

    public void setMessageDate(String messageDate) {
        this.messageDate = messageDate;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData(String rawData) {
        this.rawData = rawData;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getPlotValues() {
        return plotValues;
    }

    public void setPlotValues(String plotValues) {
        this.plotValues = plotValues;
    }

    public String getPlotLabels() {
        return plotLabels;
    }

    public void setPlotLabels(String plotLabels) {
        this.plotLabels = plotLabels;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }

    public String getPendingChange() {
        return pendingChange;
    }

    public void setPendingChange(String pendingChange) {
        this.pendingChange = pendingChange;
    }
}
