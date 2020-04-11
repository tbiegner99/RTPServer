package com.rtp.packet;

import com.tj.mp4.MediaInfo;
import com.tj.mp4.TrackInfo;

import java.io.IOException;

public abstract class RTPPacketGenerator implements RTPPacketizer {

    private RTPPacketizer sampleReader;
    private MediaInfo mediaInfo;
    private TrackInfo trackInfo;
    private int lastTimestamp;
    private short lastSequenceNumber;
    private long lastRealTime;

    public RTPPacketGenerator(TrackInfo track) {
        this.mediaInfo = track.getMediaInfo();
        trackInfo = track;
        sampleReader = getPacketizer();
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    @Override
    public boolean hasNext() {
        return sampleReader.hasNext();
    }

    public TrackInfo getTrackInfo() {
        return trackInfo;
    }

    @Override
    public RTPPacket next() {
        RTPPacket pack = sampleReader.next();
        updateInfo(pack);
        return pack;
    }

    protected void updateInfo(RTPPacket pack) {
        if (pack != null) {
            lastTimestamp = pack.getTimestamp();
            lastRealTime = System.currentTimeMillis();
            lastSequenceNumber = pack.getSequenceNumber();
        }
    }

    @Override
    public void remove() {
    }

    public RTPPacketGenerator repeat() throws IOException {
        return prepareNextTrack(this.getTrackInfo());
    }

    public RTPPacketGenerator prepareNextTrack(TrackInfo info) throws IOException {
        long timeDiff = System.currentTimeMillis() - lastRealTime;
        int timeStampDiff = (int) ((timeDiff / 1000) * info.getSampleRate());
        return prepareNextTrack(this.getTrackInfo(), lastTimestamp + timeStampDiff, (short) (lastSequenceNumber + 1));
    }

    public abstract RTPPacketGenerator prepareNextTrack(TrackInfo info, int timestamp, short seqNum) throws IOException;

    public abstract RTPPacketizer getPacketizer();

}
