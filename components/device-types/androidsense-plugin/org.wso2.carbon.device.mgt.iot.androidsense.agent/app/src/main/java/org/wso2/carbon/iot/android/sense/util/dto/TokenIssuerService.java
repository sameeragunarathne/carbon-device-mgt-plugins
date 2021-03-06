/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 *
 */

package org.wso2.carbon.iot.android.sense.util.dto;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * This hold the api definition that is used as a contract with netflix feign.
 */
@Path("/token")
public interface TokenIssuerService {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    AccessTokenInfo getToken(@QueryParam("grant_type") String grant, @QueryParam("username") String username,
            @QueryParam("password") String password, @QueryParam("scope") String scope);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    AccessTokenInfo getRefreshToken(@QueryParam("grant_type") String grantType, @QueryParam("refreshToken") String refreshToken);


}
