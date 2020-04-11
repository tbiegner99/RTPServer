package com.tj.mp4;

public class MediaEdit {

    private long duration;
    private long mediaTime;
    private short mediaRateInt;
    private short mediaRateFraction;

    public MediaEdit(long duration, long mediaTime, short mediaRateInt, short mediaRateFraction) {
        this.duration = duration;
        this.mediaTime = mediaTime;
        this.mediaRateInt = mediaRateInt;
        this.mediaRateFraction = mediaRateFraction;
    }

    public long getDuration() {
        return duration;
    }

    public long getMediaTime() {
        return mediaTime;
    }

    public short getMediaRateInt() {
        return mediaRateInt;
    }

    public short getMediaRateFraction() {
        return mediaRateFraction;
    }

}
