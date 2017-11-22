package org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class Cred implements Serializable {
    @ApiModelProperty(
            name = "username",
            value = "username of the monnit account",
            required = true
    )
    private String username;
    @ApiModelProperty(
            name = "password",
            value = "password of the monnit account",
            required = true
    )
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
