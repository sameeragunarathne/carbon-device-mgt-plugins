package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.group.mgt.DeviceGroup;

import java.io.Serializable;
import java.util.List;

public class MonnitDeviceGroup implements Serializable {
    String deviceName;
    int gatewayID;
    DeviceGroup deviceGroup;
    List<Device> devices;
    List<Integer> sensorIds;
    DeviceLocation location;

    public DeviceGroup getDeviceGroup() {
        return deviceGroup;
    }

    public void setDeviceGroup(DeviceGroup deviceGroup) {
        this.deviceGroup = deviceGroup;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public List<Integer> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<Integer> sensorIds) {
        this.sensorIds = sensorIds;
    }

    public DeviceLocation getLocation() {
        return location;
    }

    public void setLocation(DeviceLocation location) {
        this.location = location;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getGatewayID() {
        return gatewayID;
    }

    public void setGatewayID(int gatewayID) {
        this.gatewayID = gatewayID;
    }
}
