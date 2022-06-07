package fhv.musicshop;

import fhv.musicshop.domain.JwtManager;
import fhv.musicshop.domain.Role;
import fhv.musicshop.domain.Song;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Path("")
public class RestController {

    @GET
    @Path("/welcome")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Welcome to the download-microservice :)";
    }

    @Transactional
    @GET
    @Path("/download/{songId}")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Song download initiated",
                            content = {
                                    @Content(
                                            mediaType = MediaType.MEDIA_TYPE_WILDCARD,
                                            schema = @Schema(implementation = byte.class)
                                    )
                            }
                    ),
                    @APIResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = {
                                    @Content(
                                            mediaType = MediaType.TEXT_PLAIN,
                                            schema = @Schema(implementation = String.class)
                                    )
                            }
                    ),
                    @APIResponse(
                            responseCode = "403",
                            description = "No permission",
                            content = {
                                    @Content(
                                            mediaType = MediaType.TEXT_PLAIN,
                                            schema = @Schema(implementation = String.class)
                                    )
                            }
                    ),
                    @APIResponse(
                            responseCode = "404",
                            description = "Song not found",
                            content = {
                                    @Content(
                                            mediaType = MediaType.TEXT_PLAIN,
                                            schema = @Schema(implementation = String.class)
                                    )
                            }
                    ),
                    @APIResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = {
                                    @Content(
                                            mediaType = MediaType.TEXT_PLAIN,
                                            schema = @Schema(implementation = String.class)
                                    )
                            }
                    )
            })
    public Response getFile(@PathParam("songId") long songId,
                            @HeaderParam("Authorization") String jwt)
    {
        if (null == jwt || !JwtManager.isValidToken(jwt)) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid jwt provided")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        if (!isCustomer(jwt)) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("No permission")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        SongService songService = new SongServiceImpl();
        Optional<Song> songOptional = songService.getSongById(songId);

        if (songOptional.isEmpty()){
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Song not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        String ownerId = JwtManager.getEmailAddress(jwt);

        if (!songService.isSongOwned(songId,ownerId)){
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("Song not bought")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        URL resource = getClass().getClassLoader().getResource(songOptional.get().getFileName());
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(songOptional.get().getFileName());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buffer = new byte[4096];
            while ((len = is.read(buffer, 0, buffer.length)) != -1) {
                baos.write(buffer, 0, len);
            }
            Response.ResponseBuilder response = Response
                    .status(Response.Status.OK)
                    .entity(baos)
                    .type(MediaType.APPLICATION_OCTET_STREAM);
            response.header("Content-Disposition", "attachment; filename=\""+songOptional.get().getFileName()+"\"");

            return response.build();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Internal Server Error")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }

    private boolean isCustomer(String jwt_Token) {
        List<Role> userRoles = JwtManager.getRoles(jwt_Token);

        return userRoles.contains(Role.CUSTOMER);
    }

    private File getResourceAsFile(String resourcePath) throws IOException {

        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            return null;
        }

        File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
        tempFile.deleteOnExit();

        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            //copy stream
            byte[] buffer = new byte[1024*8];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;

    }
}