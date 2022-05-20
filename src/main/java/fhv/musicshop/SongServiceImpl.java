package fhv.musicshop;

import fhv.musicshop.domain.Song;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.core.Response;
import java.util.Optional;

public class SongServiceImpl implements SongService{
    @Override
    public Optional<Song> getSongById(long id) {
        return Song.find("id",id).firstResultOptional();
    }

    @Override
    public boolean isSongOwned(long song, String ownerId) {
        ResteasyClient httpClient = new ResteasyClientBuilder().build();
        String url = "http://localhost:9001/song/owned/"+song;
        Response response = httpClient.target(url).request().header("ownerId",ownerId).get();

        return response.readEntity(Boolean.class);
    }
}
