package com.rtp.stream.playlist;

import com.rtp.stream.playlist.source.PlaylistSource;
import com.rtsp.server.ApplicationProperties;
import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;

public class KareokePlaylist implements Playlist {
    private final PlaylistSource source;
    private Optional<MediaInfo> currentItem;
    private boolean isCurrentItemPlaceholder = false;

    private LinkedList<BasicPlaylistItem> items = new LinkedList<>();

    public KareokePlaylist(PlaylistSource playlistSource) {
        this.source = playlistSource;
        //items.add(new BasicPlaylistItem(getFullFilePath("/screensavers/test/converted/testkar3.mp4")));
        //items.add(new BasicPlaylistItem(getFullFilePath("/screensavers/test/converted/lighteffect9.m4v")));
        // items.add(new BasicPlaylistItem(getFullFilePath("/screensavers/test/converted/testkar2.mp4")));
        // items.add(new BasicPlaylistItem(getFullFilePath("/screensavers/test/converted/atoms4.m4v")));
        //items.add(new BasicPlaylistItem(getFullFilePath("/screensavers/test/converted/testkar.m4v")));
        //items.add(new KareokePlaylistItem(getFullFilePath("/screensavers/test/converted/testkar.m4v")));
    }

    @Override
    public void setRepeatMode(RepeatMode mode) {
    }

    @Override
    public void addItem(PlaylistItem item) {
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    public String getFullFilePath(String relativePath) {
        StringBuilder baseDir = new StringBuilder(ApplicationProperties.getProperty("VIDEO_PATH"));
        baseDir.append(relativePath);
        return baseDir.toString();
    }


    public String getPlaceholderVideo() {
        return ApplicationProperties.getProperty("PLACEHOLDER_VIDEO");
    }

    @Override
    public boolean canSkip() {
        return !isCurrentItemPlaceholder;
    }

    @Override
    public MediaInfo next() throws IOException {
        String resource;
        if (isCurrentItemPlaceholder) {
            try {
                Optional<PlaylistItem> item = source.dequeue();
                isCurrentItemPlaceholder = !item.isPresent();
                resource = item.map(PlaylistItem::getFileLocation).orElse(getPlaceholderVideo());
            } catch (IOException e) {
                resource = getPlaceholderVideo();
                isCurrentItemPlaceholder = true;
            }
        } else {
            resource = getPlaceholderVideo();
            isCurrentItemPlaceholder = true;
        }

        currentItem = Optional.of(MP4Reader.generateMediaInfo(resource));
        return getCurrentItem();

    }

    @Override
    public MediaInfo getCurrentItem() {
        return currentItem.orElse(null);
    }
}
