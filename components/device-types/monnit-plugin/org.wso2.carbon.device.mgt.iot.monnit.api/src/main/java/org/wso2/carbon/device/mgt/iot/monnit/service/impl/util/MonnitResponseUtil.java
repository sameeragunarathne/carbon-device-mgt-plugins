package org.wso2.carbon.device.mgt.iot.monnit.service.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensor;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.Result;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.List;

public class MonnitResponseUtil<T> {
    private static Log log = LogFactory.getLog(MonnitResponseUtil.class);

    public T generateResultObj(MonnitResponse resp) throws JAXBException {
        ElementNSImpl payload = ((ElementNSImpl) resp.getResult());
        JAXBContext payloadContext = JAXBContext.newInstance(Result.class);
        Result result = (Result)payloadContext.createUnmarshaller().unmarshal(payload);
        if(resp.getMethod().equals("SensorList")) {
            return (T) result.getApiSensors();
        } else if(resp.getMethod().equals("SensorGet")) {
            return (T) result.getApiSensor();
        }
        return null;
    }
}
