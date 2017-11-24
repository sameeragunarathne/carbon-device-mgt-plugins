package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "APISensorList")
public class ApiSensorList {

    private List<ApiSensor> apiSensors;

    @XmlElementWrapper
    @XmlElement(name = "APISensor")
    public List<ApiSensor> getApiSensors() {
        return apiSensors;
    }

    public void setApiSensors(List<ApiSensor> apiSensors) {
        this.apiSensors = apiSensors;
    }
}
