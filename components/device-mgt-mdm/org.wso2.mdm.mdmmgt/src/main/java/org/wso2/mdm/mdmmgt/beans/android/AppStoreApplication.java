/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.mdm.mdmmgt.beans.android;

import com.google.gson.Gson;
import org.wso2.mdm.mdmmgt.common.MDMException;

import java.io.Serializable;

/**
 * This class represents the Appstore Application information.
 */
public class AppStoreApplication implements Serializable {

    private String type;
    private String appIdentifier;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAppIdentifier() {
        return appIdentifier;
    }

    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
    }

    public String toJSON() throws MDMException {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
