package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import org.wso2.carbon.device.mgt.iot.monnit.service.impl.util.APIUtil;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public class MonnitServiceImpl implements MonnitService {

    @Override
    @GET
    @Path("/monnit/{token}/sensors")
    public Response getAllSensors(@PathParam("token")String token, @QueryParam("name") String name,
            @QueryParam("applicationID") String applicationId) {
        String response = APIUtil.getSensorsList(token, name, applicationId);
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @POST
    @Path("/monnit/{token}/sensors")
    public Response assignSensor(@PathParam("token")String token, @QueryParam("networkID") String networkId,
            @QueryParam("sensorID") String sensorId, @QueryParam("checkDigit") String checkDigit) {
        boolean response = APIUtil.registerDevice(token, networkId, sensorId,checkDigit);
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }

    @Override
    @GET
    @Path("/monnit/auth-token")
    public Response getAuthToken(@QueryParam("username") String username, @QueryParam("password") String password) {
        String token = APIUtil.getAuthToken(username, password);
        return Response.status(Response.Status.OK.getStatusCode()).entity(token).build();
    }
}
