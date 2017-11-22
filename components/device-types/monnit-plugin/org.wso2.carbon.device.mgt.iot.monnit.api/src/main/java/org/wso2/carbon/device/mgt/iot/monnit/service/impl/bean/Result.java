package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import java.util.List;

@XmlRootElement(name = "Result")
public class Result {
    private List<ApiSensor> apiSensors;

    @XmlElementWrapper(name = "APISensorList")
    @XmlElement(name = "APISensor")
    public List<ApiSensor> getApiSensors() {
        return apiSensors;
    }


    public void setApiSensors(List<ApiSensor> apiSensors) {
        this.apiSensors = apiSensors;
    }
}
