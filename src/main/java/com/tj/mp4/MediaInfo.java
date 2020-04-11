package com.tj.mp4;

import com.tj.mp4.TrackInfo.TrackInfoBuilder;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class MediaInfo {
    private final File file;
    private List<TrackInfo> tracks;
    private Float duration;
    private long creationTime;
    public RandomAccessFile source;
    private ReentrantLock lock;

    public static MediaInfoBuilder builder(File fileInfo) {
        return new MediaInfoBuilder(fileInfo);
    }

    private MediaInfo(File file) {
        tracks = new ArrayList<TrackInfo>();
        this.lock = new ReentrantLock();
        this.file = file;
    }

    public Float getDurationInSeconds() {
        return duration;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getDurationMillis() {
        return (long) (getDurationInSeconds() * 1000);
    }

    public List<TrackInfo> getTracks() {
        return tracks;
    }

    public void resetSampleReaders() {
        tracks.forEach(TrackInfo::resetSampleReader);
    }


    public Optional<TrackInfo> getVideoTrack() {
        return getTracks().stream().filter(track -> track.isVideoTrack()).findFirst();
    }

    public List<TrackInfo> getAudioTracks() {
        return getTracks().stream().filter(track -> track.isAudioTrack()).collect(Collectors.toList());
    }

    public Optional<TrackInfo> getAudioTrackWithMediaType(MediaType mediaType) {
        return getTracks().stream()
                .filter(track -> track.isAudioTrack() && track.getMediaType() == mediaType)
                .findFirst();
    }

    public URI getURI() {
        return this.file.toURI();
    }

    public RandomAccessFile getSourceThreadSafe() {
        lock.lock();
        return this.source;
    }

    public void unlockSource() {
        lock.unlock();
    }

    public static class MediaInfoBuilder {

        private MediaInfo item;
        private TrackInfoBuilder currentTrack;

        private MediaInfoBuilder(File fileInfo) {
            this.item = new MediaInfo(fileInfo);
        }

        public boolean hasCurrentTrack() {
            return currentTrack != null;
        }

        public TrackInfoBuilder newTrack() {
            currentTrack = TrackInfo.builder(this);
            item.tracks.add(currentTrack.build());
            return currentTrack;
        }

        public TrackInfoBuilder currentTrack() {
            if (!hasCurrentTrack()) {
                return newTrack();
            }
            return currentTrack;
        }

        public MediaInfoBuilder clearCurrentTrack() {
            currentTrack = null;
            return this;
        }

        public MediaInfoBuilder setLengthInSeconds(float lengthInSeconds) {
            item.duration = lengthInSeconds;
            return this;
        }

        public MediaInfoBuilder setCreationTime(long creationTime) {
            item.creationTime = creationTime;
            return this;
        }

        public MediaInfo build() {
            return this.item;
        }

        public MediaInfoBuilder source(RandomAccessFile fileInputStream) {
            item.source = fileInputStream;
            return this;
        }


        public RandomAccessFile getSource() {
            return item.source;
        }

    }

}
