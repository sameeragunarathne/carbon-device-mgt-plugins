package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import io.swagger.annotations.ApiModelProperty;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;

import java.io.Serializable;

public class MonnitDevice implements Serializable {

    @ApiModelProperty(
            name = "networkID",
            value = "Identifier of network on your account",
            required = true
    )
    private String networkID;
    @ApiModelProperty(
            name = "gatewayID",
            value = "Identifier of gateway to move",
            required = true
    )
    private String gatewayID;
    @ApiModelProperty(
            name = "sensorID",
            value = "Identifier of sensor to move",
            required = true
    )
    private String sensorID;
    @ApiModelProperty(
            name = "checkDigit",
            value = "Check digit to prevent unauthorized movement of sensors",
            required = true
    )
    private String checkDigit;
    @ApiModelProperty(
            name = "type",
            value = "device type (i.e sensor/gateway)",
            required = true
    )
    private String type;
    @ApiModelProperty(
            name = "location",
            value = "location of the sensor",
            required = true
    )
    private DeviceLocation location;

    public String getNetworkID() {
        return networkID;
    }

    public void setNetworkID(String networkID) {
        this.networkID = networkID;
    }

    public String getGatewayID() {
        return gatewayID;
    }

    public void setGatewayID(String gatewayID) {
        this.gatewayID = gatewayID;
    }

    public String getSensorID() {
        return sensorID;
    }

    public void setSensorID(String sensorID) {
        this.sensorID = sensorID;
    }

    public String getCheckDigit() {
        return checkDigit;
    }

    public void setCheckDigit(String checkDigit) {
        this.checkDigit = checkDigit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DeviceLocation getLocation() {
        return location;
    }

    public void setLocation(DeviceLocation location) {
        this.location = location;
    }
}
