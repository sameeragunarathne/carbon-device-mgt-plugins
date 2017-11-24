package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "APIGatewayList")
public class ApiGatewayList {

    private List<ApiGateway> apiGateways;

    @XmlElementWrapper
    @XmlElement(name = "APIGateway")
    public List<ApiGateway> getApiGateways() {
        return apiGateways;
    }

    public void setApiGateways(List<ApiGateway> apiGateways) {
        this.apiGateways = apiGateways;
    }
}
