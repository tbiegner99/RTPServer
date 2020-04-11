package com.models.http.response;

import com.rtp.stream.playlist.BasicPlaylistItem;
import com.rtp.stream.playlist.PlaylistItem;
import com.rtsp.server.ApplicationProperties;

public class KareokePlaylistItemResponse {
    private String artist;
    private String id;
    private String title;
    private String source;
    private String filename;

    public KareokePlaylistItemResponse() {
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFilename() {
        return ApplicationProperties.getProperty("KAREOKE_PATH") + "/" + filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /*private String getFilename() {
        String filename = artist + " - " + title + " [" + source + "].mp4";
        return ApplicationProperties.getProperty("KAREOKE_PATH") + "/" + filename;
    }*/

    public PlaylistItem toPlaylistItem() {
        return new BasicPlaylistItem(id, title, artist, getFilename());
    }
}
