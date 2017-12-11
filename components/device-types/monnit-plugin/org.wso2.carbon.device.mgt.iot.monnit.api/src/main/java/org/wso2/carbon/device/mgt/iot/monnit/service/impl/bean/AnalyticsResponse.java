package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import java.io.Serializable;
import java.util.HashMap;

public class AnalyticsResponse implements Serializable{
    long timestamp;
    HashMap<String, String> values;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, String> getValues() {
        return values;
    }

    public void setValues(HashMap<String, String> values) {
        this.values = values;
    }
}
