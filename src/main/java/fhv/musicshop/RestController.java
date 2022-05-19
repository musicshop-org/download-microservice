package fhv.musicshop;

import fhv.musicshop.domain.Song;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

@Path("")
public class RestController {

    @GET
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
    public Response getFile(@PathParam("songId") long songId) {
        Optional<Song> songOptional = Song.find("id",songId).firstResultOptional();
        if(songOptional.isEmpty()){
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Song not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        URL resource = getClass().getClassLoader().getResource(songOptional.get().getFileName());
        try {
            File file = new File(resource.toURI());
            Response.ResponseBuilder response = Response
                    .status(Response.Status.OK)
                    .entity(file)
                    .type(MediaType.MEDIA_TYPE_WILDCARD);
            response.header("Content-Disposition", "attachment; filename=\""+songOptional.get().getFileName()+"\"");

            return response.build();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Internal Server Error")
                .type(MediaType.TEXT_PLAIN)
                .build();
    }
}