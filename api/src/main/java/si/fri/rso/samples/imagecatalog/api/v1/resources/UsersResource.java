package si.fri.rso.samples.imagecatalog.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import si.fri.rso.samples.imagecatalog.lib.Session;
import si.fri.rso.samples.imagecatalog.lib.User;
import si.fri.rso.samples.imagecatalog.models.responses.SessionResponse;
import si.fri.rso.samples.imagecatalog.models.entities.UserAuthInput;
import si.fri.rso.samples.imagecatalog.services.beans.SessionBean;
import si.fri.rso.samples.imagecatalog.services.beans.UsersBean;
import si.fri.rso.samples.imagecatalog.services.services.PasswordService;

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

    @Inject
    private SessionBean sessionBean;


    @Context
    protected UriInfo uriInfo;

    @Operation(description = "Get all users.", summary = "Get all users")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of users",
                    content = @Content(schema = @Schema(implementation = User.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    public Response getUsers() {

        List<User> users = usersBean.getUsers(uriInfo);

        return Response.status(Response.Status.OK).entity(users)
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
    public Response getUser(@Parameter(description = "User ID.", required = true) @PathParam("userId") Integer userId) {

        User user = usersBean.getUser(userId);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(user)
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
            user.setPassword(PasswordService.hashPassword(user.getPassword()));
            user = usersBean.createUser(user);
        }

        return createSessionResponse(user);
    }


    @Operation(description = "Login user using username and password.", summary = "Login user")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Metadata successfully added."
            ),
            @APIResponse(responseCode = "403", description = "Wrong credentials .")
    })
    @POST
    @Path("/login")
    public Response login(@RequestBody(
            description = "DTO object with username and password.",
            required = true, content = @Content(
            schema = @Schema(implementation = UserAuthInput.class))) UserAuthInput userAuthInput) {

        if ((userAuthInput.getEmail() == null || userAuthInput.getPassword() == null)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        User user = usersBean.getUserByEmail(userAuthInput.getEmail());

        if (!PasswordService.verifyPassword(userAuthInput.getPassword(), user.getPassword())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // delete all existing sessions
        List<Session> existingSessions = sessionBean.getSessionsByUserId(user.getUserId());

        if (!existingSessions.isEmpty()) {
            for (Session session : existingSessions) {
                sessionBean.deleteSession(session.getId());
            }
        }

        // create new session
        return createSessionResponse(user);
    }

    private Response createSessionResponse(User user) {
        Session session = new Session(user.getUserId());
        session = sessionBean.createSession(session);

        if (session == null) {
            return Response.status(Response.Status.CONFLICT).build();
        }

        SessionResponse sessionEntity = new SessionResponse(user.getUserId(), user.getEmail(), session.getId(), session.getValidUntil());

        return Response.status(Response.Status.OK).entity(sessionEntity)
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
                    .build();
        }

        return Response.status(Response.Status.NOT_MODIFIED)
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
    @Path("/{userId}")
    public Response deleteUser(@Parameter(description = "User ID.", required = true)
                                        @PathParam("userId") Integer userId){

        boolean deleted = usersBean.deleteUser(userId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT)
                    .build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }


    @Operation(description = "Get all sessions.", summary = "Get all sessions")
    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "List of sessions",
                    content = @Content(schema = @Schema(implementation = Session.class, type = SchemaType.ARRAY)),
                    headers = {@Header(name = "X-Total-Count", description = "Number of objects in list")}
            )})
    @GET
    @Path("/sessions")
    public Response getSessions() {

        List<Session> sessions = sessionBean.getSessions();

        return Response.status(Response.Status.OK).entity(sessions)
                .build();
    }


    @Operation(description = "Get session.", summary = "Get session")

    @APIResponses({
            @APIResponse(responseCode = "200",
                    description = "Session",
                    content = @Content(
                            schema = @Schema(implementation = Session.class))
            )})
    @GET
    @Path("/sessions/{sessionId}")
    public Response getSession(@Parameter(description = "Session ID.", required = true) @PathParam("sessionId") Integer sessionId) {

        Session session = sessionBean.getSession(sessionId);

        if (session == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(session)
                .build();
    }

    @Operation(description = "Delete session.", summary = "Delete session")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Session successfully deleted."
            ),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found."
            )
    })
    @DELETE
    @Path("/sessions/{sessionId}")
    public Response deleteSession(@Parameter(description = "Session ID.", required = true)
                               @PathParam("sessionId") String sessionId){

        boolean deleted = sessionBean.deleteSession(sessionId);

        if (deleted) {
            return Response.status(Response.Status.NO_CONTENT)
                    .build();
        }
        else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }
}
