package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "APIGateway")
public class ApiGateway {
    private String gatewayId;
    private String networkId;
    private String name;
    private String gatewayType;
    private String heartBeat;
    private String isDirty;
    private String lastCommunicationDate;
    private String lastInboundIp;
    private String mac;
    private String isUnlocked;
    private String checkDigit;
    private String accId;
    private String signalStrength;
    private String batteryLevel;

    @XmlAttribute(name = "GatewayID")
    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    @XmlAttribute(name = "NetworkID")
    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    @XmlAttribute(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name = "GatewayType")
    public String getGatewayType() {
        return gatewayType;
    }

    public void setGatewayType(String gatewayType) {
        this.gatewayType = gatewayType;
    }

    @XmlAttribute(name = "Heartbeat")
    public String getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(String heartBeat) {
        this.heartBeat = heartBeat;
    }

    @XmlAttribute(name = "IsDirty")
    public String getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(String isDirty) {
        this.isDirty = isDirty;
    }

    @XmlAttribute(name = "LastCommunicationDate")
    public String getLastCommunicationDate() {
        return lastCommunicationDate;
    }

    public void setLastCommunicationDate(String lastCommunicationDate) {
        this.lastCommunicationDate = lastCommunicationDate;
    }

    @XmlAttribute(name = "LastInboundIPAddress")
    public String getLastInboundIp() {
        return lastInboundIp;
    }

    public void setLastInboundIp(String lastInboundIp) {
        this.lastInboundIp = lastInboundIp;
    }

    @XmlAttribute(name = "MacAddress")
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @XmlAttribute(name = "IsUnlocked")
    public String getIsUnlocked() {
        return isUnlocked;
    }

    public void setIsUnlocked(String isUnlocked) {
        this.isUnlocked = isUnlocked;
    }

    @XmlAttribute(name = "CheckDigit")
    public String getCheckDigit() {
        return checkDigit;
    }

    public void setCheckDigit(String checkDigit) {
        this.checkDigit = checkDigit;
    }

    @XmlAttribute(name = "AccountID")
    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    @XmlAttribute(name = "SignalStrength")
    public String getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }

    @XmlAttribute(name = "BatteryLevel")
    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
}
