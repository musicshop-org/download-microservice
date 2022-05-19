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
import java.util.Optional;

@Path("/welcome")
public class RestController {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Welcome to the download-microservice :)";
    }

    @Transactional
    @GET
    @Path("/download/{songId}")
    public Response getFile(@PathParam("songId") String songId) {
        Optional<Song> songOptional = Song.find("id",songId).firstResultOptional();
        if(songOptional.isEmpty()){
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("Song not found")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }

        return Response
                .status(Response.Status.OK)
                .entity(songOptional.get())
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}