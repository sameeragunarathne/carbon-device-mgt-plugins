package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import java.io.Serializable;
import java.util.List;

public class QueryPayload implements Serializable {
    String tableName;
    String query;
    int start;
    int count;
    List<SortBy> sortBy;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<SortBy> getSortBy() {
        return sortBy;
    }

    public void setSortBy(List<SortBy> sortBy) {
        this.sortBy = sortBy;
    }
}
