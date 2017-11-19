package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSchemaTypes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "SensorRestAPI")
public class MonnitResponse<T> {

    private String method;
    private T result;
//    private List resultList;

    @XmlElement(name = "Method", required = true)
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @XmlElement(name = "Result")
    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

//    @XmlElement(name = "Result")
//    @XmlSchemaType(name = "collection")
//    public List getResultList() {
//        return resultList;
//    }
//
//    public void setResultList(List resultList) {
//        this.resultList = resultList;
//    }
}
