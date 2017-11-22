package org.wso2.carbon.device.mgt.iot.monnit.service.impl;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Extension;
import io.swagger.annotations.ExtensionProperty;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.annotations.Tag;
import org.wso2.carbon.apimgt.annotations.api.Scope;
import org.wso2.carbon.apimgt.annotations.api.Scopes;
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitDevice;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@SwaggerDefinition(
        info = @Info(
                version = "1.0.0",
                title = "",
                extensions = {
                        @Extension(properties = {
                                @ExtensionProperty(name = "name", value = "monnit"),
                                @ExtensionProperty(name = "context", value = "/monnit"),
                        })
                }
        ),
        tags = {
                @Tag(name = "monnit, device_management", description = "")
        }
)
@Scopes(
        scopes = {
                @Scope(
                        name = "Enroll device",
                        description = "",
                        key = "perm:monnit:enroll",
                        permissions = {"/device-mgt/devices/enroll/monnit"}
                )
        }
)
public interface MonnitService {
    String SCOPE = "scope";

    @GET
    @Path("/monnit/{token}/sensors")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get all sensors",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response getAllSensors(@PathParam("token")String token, @QueryParam("name") String name, @QueryParam("applicationID") String applicationId);

    @GET
    @Path("/monnit/{token}/gateways")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get all gateways",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response getAllGateways(@PathParam("token")String token, @QueryParam("name") String name);

    @POST
    @Path("/monnit/{token}/sensors")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Assign a sensor",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response assignSensor(@PathParam("token")String token,  MonnitDevice device);

    @POST
    @Path("/monnit/{token}/gateways")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Assign a gateway",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response assignGateway(@PathParam("token")String token,  MonnitDevice device);

    @GET
    @Path("/monnit/auth-token")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get auth token",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response getAuthToken(@QueryParam("username") String username, @QueryParam("password") String password);
}
