package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.List;

@XmlRootElement(name = "Result")
public class Result {
    private List<ApiSensor> apiSensors;
    private ApiSensor apiSensor;
    private List<ApiGateway> apiGateways;
    private ApiGateway apiGateway;
    private List<APISentNotification> apiSentNotifications;

    @XmlElementWrapper(name = "APISensorList")
    @XmlElement(name = "APISensor")
    public List<ApiSensor> getApiSensors() {
        return apiSensors;
    }

    public void setApiSensors(List<ApiSensor> apiSensors) {
        this.apiSensors = apiSensors;
    }

    public ApiSensor getApiSensor() {
        return apiSensor;
    }

    @XmlElement(name = "APISensor")
    public void setApiSensor(ApiSensor apiSensor) {
        this.apiSensor = apiSensor;
    }

    @XmlElementWrapper(name = "APIGatewayList")
    @XmlElement(name = "APIGateway")
    public List<ApiGateway> getApiGateways() {
        return apiGateways;
    }

    public void setApiGateways(List<ApiGateway> apiGateways) {
        this.apiGateways = apiGateways;
    }

    @XmlElement(name = "APIGateway")
    public ApiGateway getApiGateway() {
        return apiGateway;
    }

    public void setApiGateway(ApiGateway apiGateway) {
        this.apiGateway = apiGateway;
    }

    @XmlElementWrapper(name = "APISentNotificationList")
    @XmlElement(name = "APISentNotification")
    public List<APISentNotification> getApiSentNotifications() {
        return apiSentNotifications;
    }

    public void setApiSentNotifications(List<APISentNotification> apiSentNotifications) {
        this.apiSentNotifications = apiSentNotifications;
    }
}
