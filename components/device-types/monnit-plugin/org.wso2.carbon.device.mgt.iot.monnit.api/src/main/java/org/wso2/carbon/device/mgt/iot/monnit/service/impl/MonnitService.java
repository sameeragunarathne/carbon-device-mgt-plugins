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
import org.wso2.carbon.device.mgt.iot.monnit.service.impl.bean.MonnitDeviceGroup;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
    @Path("/monnit/init")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Initialize and sync IoT server with iMonnit cloud",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response init(@QueryParam("token") String token);

    @GET
    @Path("/monnit/sensors")
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
    Response getAllSensors(@QueryParam("token") String token, @QueryParam("name") String name, @QueryParam("applicationID") String applicationId,@QueryParam("networkID") String networkID, @QueryParam("status") String status);

    @GET
    @Path("/monnit/gateways")
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
    Response getAllGateways(@QueryParam("token") String token, @QueryParam("name") String name, @QueryParam("networkID") String networkID);

    @POST
    @Path("/monnit/sensors")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Assign a sensor to a network",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response assignSensor(@QueryParam("token") String token,  MonnitDevice device);

    @POST
    @Path("/monnit/gateways")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Assign a gateway to a network",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response assignGateway(@QueryParam("token") String token,  MonnitDevice device);

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

    @POST
    @Path("/monnit/devices")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "POST",
            value = "Register a device group",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response addDeviceGroup(@QueryParam("gatewayID") int gatewayId, @QueryParam("deviceName") String deviceName, MonnitDeviceGroup grp);

    @GET
    @Path("/monnit/devices")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get a device group",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response getDeviceGroups(@QueryParam("gatewayID") int gatewayId, @QueryParam("deviceName") String deviceName);

    @GET
    @Path("/monnit/webhook/create")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Create webhook in imonnit cloud to receive data",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response createWebHook(@QueryParam("token") String token, @QueryParam("baseUrl") String baseUrl);


    @GET
    @Path("/monnit/recent-notifications")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get recent notifications for iMonnit sensor",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response getRecentNotifications(@QueryParam("minutes") String minutes, @QueryParam("lastNotificationID") String lastNotificationId, @QueryParam("sensorID") String sensorId);

    @GET
    @Path("/monnit/notifications")
    @ApiOperation(
            consumes = MediaType.APPLICATION_JSON,
            httpMethod = "GET",
            value = "Get notifications for iMonnit sensor",
            notes = "",
            response = Response.class,
            tags = "monnit",
            extensions = {
                    @Extension(properties = {
                            @ExtensionProperty(name = SCOPE, value = "perm:monnit:enroll")
                    })
            }
    )
    Response getNotifications(@QueryParam("from") String from, @QueryParam("to") String to, @QueryParam("sensorID") String sensorID);
}
