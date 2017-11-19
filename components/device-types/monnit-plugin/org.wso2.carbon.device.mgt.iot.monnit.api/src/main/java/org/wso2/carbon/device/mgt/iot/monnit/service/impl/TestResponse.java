package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import org.apache.xerces.dom.ElementNSImpl;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.ApiSensorList;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class TestResponse {
    private static MonnitResponse generateResponseObj(String resp) throws JAXBException {
        JAXBContext ctx = JAXBContext.newInstance(MonnitResponse.class);
        Unmarshaller unmarshaller = ctx.createUnmarshaller();
        StringReader reader = new StringReader(resp);
        MonnitResponse respObj = (MonnitResponse) unmarshaller.unmarshal(reader);
        return respObj;
    }

    private static MonnitResponse generateResultObj(MonnitResponse resp) throws JAXBException {
        ElementNSImpl payload = ((ElementNSImpl) resp.getResult());
        if(payload.getFirstChild().getNodeName().equals("ApiSensorList")) {
            JAXBContext payloadContext = JAXBContext.newInstance(ApiSensorList.class);
            ApiSensorList list = (ApiSensorList)payloadContext.createUnmarshaller().unmarshal(payload);
        }
        return null;
    }

//    public static void main(String[] args) {
//        String resp = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
//                + "<SensorRestAPI xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
//                + "  <Method>SensorGet</Method>\n"
//                + "  <Result xsi:type=\"xsd:string\"><APISensor SensorID=\"101\" MonnitApplicationID=\"2\" CSNetID=\"100\" SensorName=\"Room3_Ceiling\" LastCommunicationDate=\"6/1/2011 4:09:41 PM\" NextCommunicationDate=\"6/13/2011 6:11:41 PM\" LastDataMessageID=\"1\" PowerSourceID=\"1\" Status=\"1\" CanUpdate=\"True\" CurrentReading=\"122° F\" BatteryLevel=\"0\" SignalStrength=\"-36\" AlertsActive=\"True\" CheckDigit=\"IMABCD\" /></Result>\n"
//                + "</SensorRestAPI>";
//        try {
//            MonnitResponse response = generateResponseObj(resp);
//            generateResultObj(response);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
//    }
}
