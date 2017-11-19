/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.iot.monnit.service.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

/**
 * Handle the cxf level exceptions.
 */
public class GlobalThrowableMapper implements ExceptionMapper {
    private static final Log log = LogFactory.getLog(GlobalThrowableMapper.class);

    @Override
    public Response toResponse(Throwable e) {
        //unknown exception log and return
        log.error("An Unknown exception has been captured by global exception mapper.", e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).header("Content-Type", "application/json").build();
    }
}
