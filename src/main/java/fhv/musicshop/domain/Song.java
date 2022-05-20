package fhv.musicshop.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

import javax.persistence.*;


@Entity
public class Song extends PanacheEntityBase {

    @Id
    private long id;
    private String fileName;

    public Song() {
    }

    public Song(long id, String fileName) {
        this.id = id;
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public long getId() {
        return id;
    }
}
