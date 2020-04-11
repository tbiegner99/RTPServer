package com.rtp.stream.playlist;

import com.tj.mp4.MP4Reader;
import com.tj.mp4.MediaInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemPlaylist implements Playlist {
    private List<String> resources;
    private Integer currentIndex = 0;
    private MediaInfo currentItem;
    private RepeatMode repeatMode = RepeatMode.NONE;

    public ItemPlaylist() {

        resources = new ArrayList<String>();
    }

    @Override
    public void setRepeatMode(RepeatMode mode) {
        repeatMode = mode;
    }

    public void addResources(String... resources) {
        this.resources.addAll(Arrays.asList(resources));
    }

    @Override
    public void addItem(PlaylistItem item) {

    }

    @Override
    public boolean hasNext() {
        if (resources.isEmpty()) {
            return false;
        } else if (currentIndex < resources.size()) {
            return true;
        } else if (repeatMode.equals(RepeatMode.REPEAT_ITEM)) {
            return currentItem != null;
        } else {
            return repeatMode.equals(RepeatMode.REPEAT_LIST) || currentIndex < resources.size();
        }
    }

    @Override
    public MediaInfo next() throws IOException {
        if (!hasNext()) {
            return null;
        }
        if (repeatMode.equals(RepeatMode.REPEAT_ITEM) && currentItem != null) {
            currentItem.resetSampleReaders();
            return currentItem;
        }
        if (currentIndex >= resources.size()) {
            currentIndex = 0;
        }
        String resource = resources.get(currentIndex);
        currentItem = MP4Reader.generateMediaInfo(resource);
        currentIndex++;
        return currentItem;
    }

    @Override
    public MediaInfo getCurrentItem() {
        return currentItem;
    }
}
