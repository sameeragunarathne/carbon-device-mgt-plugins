package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import org.wso2.carbon.analytics.dataservice.commons.SortType;

import java.io.Serializable;

public class SortBy implements Serializable{
    String field;
    SortType sortType;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }
}
