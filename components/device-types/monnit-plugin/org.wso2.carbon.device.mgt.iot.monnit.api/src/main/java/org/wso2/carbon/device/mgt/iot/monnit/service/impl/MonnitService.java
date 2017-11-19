package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public interface MonnitService {

    @GET
    @Path("/monnit/sensors")
    Response getAllSensors(@QueryParam("name") String name, @QueryParam("applicationID") String applicationId);
}
