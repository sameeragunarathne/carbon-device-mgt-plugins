package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import java.io.Serializable;
import java.util.List;

public class SensorPayload implements Serializable {
    GatewayMessage gatewayMessage;
    List<SensorMessage> sensorMessages;

    public GatewayMessage getGatewayMessage() {
        return gatewayMessage;
    }

    public void setGatewayMessage(GatewayMessage gatewayMessage) {
        this.gatewayMessage = gatewayMessage;
    }

    public List<SensorMessage> getSensorMessages() {
        return sensorMessages;
    }

    public void setSensorMessages(List<SensorMessage> sensorMessages) {
        this.sensorMessages = sensorMessages;
    }
}
