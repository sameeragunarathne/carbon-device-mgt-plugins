package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public interface MonnitService {

    @GET
    @Path("/monnit/{token}/sensors")
    Response getAllSensors(@PathParam("token")String token, @QueryParam("name") String name, @QueryParam("applicationID") String applicationId);

    @POST
    @Path("/monnit/{token}/sensors")
    Response assignSensor(@PathParam("token")String token, @QueryParam("networkID") String networkId, @QueryParam("sensorID") String sensorId, @QueryParam("checkDigit") String checkDigit);

    @GET
    @Path("/monnit/auth-token")
    Response getAuthToken(@QueryParam("username") String username, @QueryParam("password") String password);
}
