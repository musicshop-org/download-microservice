package fhv.musicshop;

import fhv.musicshop.domain.Song;

import java.util.Optional;

public interface SongService {
    Optional<Song> getSongById(long id);
    boolean isSongOwned(long song, String ownerId);
}
