package fhv.musicshop;

import fhv.musicshop.domain.Song;

import java.util.Optional;

public interface SongService {
    Optional<Song> getSongById(String id);
    boolean isSongOwned(String song, String ownerId);
}
