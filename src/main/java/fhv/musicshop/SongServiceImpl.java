package fhv.musicshop;

import fhv.musicshop.domain.Song;

import java.util.Optional;

public class SongServiceImpl implements SongService{
    @Override
    public Optional<Song> getSongById(String id) {
        return Song.find("id",Long.parseLong(id)).firstResultOptional();
    }

    @Override
    public boolean isSongOwned(String song, String ownerId) {
        return false;
    }
}
