package org.wso2.carbon.device.mgt.iot.monnit.service.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.dom.ElementNSImpl;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensor;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.Result;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.StringWriter;
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
        } else if(resp.getMethod().equals("GatewayList")) {
            return (T) result.getApiGateways();
        } else if(resp.getMethod().equals("GatewayGet")) {
            return (T) result.getApiGateway();
        } else if(resp.getMethod().equals("RecentlySentNotifications")) {
            return (T) result.getApiSentNotifications();
        }
        return null;
    }

    public String marshalObjToStr(JAXBContext jc, Object object) throws JAXBException {
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
        marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        StringWriter sw = new StringWriter();
        marshaller.marshal(object, sw);
        return sw.toString();
    }
}
