package si.fri.rso.samples.imagecatalog.api.v1.resources;

import  com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.imagecatalog.lib.User;
import si.fri.rso.samples.imagecatalog.services.beans.UsersBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.logging.Logger;


@Log
@ApplicationScoped
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsersResource {

    private Logger log = Logger.getLogger(UsersResource.class.getName());

    @Inject
    private UsersBean usersBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all image metadata.", summary = "Get all metadata")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of image metadata",
                    content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getImageMetadata() {

        List<User> imageMetadata = usersBean.getUsersFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(imageMetadata)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }


    @Operation(description = "Get metadata for an image.", summary = "Get metadata for an image")

    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Image metadata",
                    content = @Content(
                            schema = @Schema(implementation = User.class))
            )})
    @GET
    @Path("/{userId}")
    public Response getUser(@Parameter(description = "User ID.", required = true)
                                     @PathParam("userId") Integer userId) {

        User user = usersBean.getUser(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(user)
                .header("Access-Control-Allow-Origin", "*")
                .build();
    }

    @Operation(description = "Add image metadata.", summary = "Add metadata")
    @APIResponses({
            @APIResponse(responseCode = "201",
                    description = "Metadata successfully added."
            ),
            @APIResponse(responseCode = "405", description = "Validation error .")
    })
    @POST
    public Response createUser(@RequestBody(
            description = "DTO object with user data.",
            required = true, content = @Content(
            schema = @Schema(implementation = User.class))) User user) {

        if ((user.getName() == null || user.getSurname() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        else {
            user = usersBean.createUser(user);
        }

        return Response.status(Response.Status.CONFLICT).entity(user)
                .header("Access-Control-Allow-Origin", "*")
                .build();

    }


    @Operation(description = "Update metadata for an image.", summary = "Update metadata")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Metadata successfully updated."
            )
    })
    @PUT
    @Path("{userId}")
    public Response putUser(@Parameter(description = "user ID.", required = true)
                                     @PathParam("userId") Integer imageMetadataId,
                                     @RequestBody(
                                             description = "DTO object with user data.",
                                             required = true, content = @Content(
                                             schema = @Schema(implementation = User.class)))
                                             User user){

        user = usersBean.putUser(imageMetadataId, user);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }

        return Response.status(Response.Status.NOT_MODIFIED)
                .header("Access-Control-Allow-Origin", "*")
                .build();

    }

    @Operation(description = "Delete user.", summary = "Delete user")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "User successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("{userId}")
    public Response deleteUser(@Parameter(description = "User ID.", required = true)
                                        @PathParam("userId") Integer userId){

        boolean deleted = usersBean.deleteUser(userId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
    }



    @OPTIONS
    @Path("{path : .*}")
    public Response handleCorsPreflightRequests(@Context HttpHeaders headers) {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .build();
    }

}
