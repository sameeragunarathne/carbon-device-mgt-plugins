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
        if(resp.getMethod().equals("SensorList")) {
            JAXBContext payloadContext = JAXBContext.newInstance(Result.class);
            Result list = (Result)payloadContext.createUnmarshaller().unmarshal(payload);
            List<ApiSensor> apiSensors = list.getApiSensors();
            for (ApiSensor sensor: apiSensors) {
                log.info(sensor.getSensorName());
            }
            return (T) apiSensors;
        }
        return null;
    }
}
