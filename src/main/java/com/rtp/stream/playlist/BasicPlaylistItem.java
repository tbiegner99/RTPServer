package com.rtp.stream.playlist;

public class BasicPlaylistItem implements PlaylistItem {

    private final String artist;
    private final String id;
    private final String title;
    private String filename;


    public BasicPlaylistItem(String id, String title, String artist, String filename) {
        this.filename = filename;
        this.id = id;
        this.artist = artist;
        this.title = title;
    }

    @Override
    public String getFileLocation() {
        return filename;
    }


    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getId() {
        return id;
    }
}
