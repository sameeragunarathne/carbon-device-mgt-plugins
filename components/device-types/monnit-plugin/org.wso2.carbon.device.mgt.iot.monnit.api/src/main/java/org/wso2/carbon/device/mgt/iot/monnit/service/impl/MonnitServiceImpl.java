package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import org.wso2.carbon.device.mgt.iot.monnit.service.impl.util.APIUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

public class MonnitServiceImpl implements MonnitService {

    @Override
    @GET
    @Path("/monnit/sensors")
    public Response getAllSensors(@QueryParam("name") String name,@QueryParam("appId") String applicationId) {
        String token = APIUtil.getAuthToken();
        String response = APIUtil.getMonnitResponse(token);
        return Response.status(Response.Status.OK.getStatusCode()).entity(response).build();
    }
}
