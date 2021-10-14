package com.rtp.stream;

import com.rtp.packet.RTPPacket;
import com.rtp.packet.RTPPacketGenerator;
import com.rtp.stream.socket.SocketManager;
import com.rtsp.server.ApplicationProperties;
import com.tj.mp4.MediaInfo;

import java.util.ArrayList;
import java.util.List;

public class ElementaryStream extends Thread implements MediaStream {
    private int lastSequenceNumber;
    private float initialTimeOffset = 0;
    private SocketManager socketManager;
    private boolean isTerminated = false;
    private RTPPacketGenerator generator;
    private List<MediaListener> listeners;
    private boolean isPaused;
    private StreamState state;
    private boolean isStarted = false;
    private boolean finished;

    private long trackStart;

    private StreamType streamType;

    public ElementaryStream(StreamType type, SocketManager socket) {
        this(type, socket, 0, 0);
    }

    public ElementaryStream(StreamType type, SocketManager socketManager, int lastSequenceNumber, float initialTimeOffset) {
        this.socketManager = socketManager;
        this.lastSequenceNumber = lastSequenceNumber;
        listeners = new ArrayList<>();
        this.state = StreamState.READY;
        this.streamType = type;
        this.initialTimeOffset = initialTimeOffset;
    }

    @Override
    public void setupMedia(RTPPacketGenerator generator) {
        if (this.generator == null) {
            this.generator = generator;
        }
    }

    @Override
    public RTPPacketGenerator getCurrentMedia() {
        return this.generator;
    }

    @Override
    public MediaInfo getMediaInfo() {
        return getCurrentMedia().getTrackInfo().getMediaInfo();
    }

    @Override
    public SocketManager getSocketManager() {
        return socketManager;
    }

    @Override
    public void addMediaListener(MediaListener listener) {
        this.listeners.add(listener);
    }

    public void removeMediaListener(MediaListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public StreamType getType() {
        return streamType;
    }

    public long getClockRate() {
        return generator.getTrackInfo().getTimescale();
    }

    @Override
    public int getClockTicksAtTime(float timeInSeconds) {
        return (int) (timeInSeconds * getClockRate());
    }

    @Override
    public long getRealStartTime() {
        return this.trackStart;
    }

    @Override
    public void play() {
        if (!this.isStarted) {
            start();
        }
        this.isPaused = false;
    }

    @Override
    public void start() {
        super.start();
        this.isStarted = true;
    }

    @Override
    public void pause() {
        this.isPaused = true;

    }

    @Override
    public void terminate() {
        this.isTerminated = true;

    }

    @Override
    public int getLastSequenceNumber() {
        return lastSequenceNumber;
    }

    @Override
    public StreamState getStreamState() {
        return state;
    }

    @Override
    public void run() {
        RTPPacket packet = null;
        try {
            long clockRate = getClockRate();
            float trackOffsetSeconds = generator.getTrackInfo().getOffset() / 1000f;
            int timestampOffset = (int) ((initialTimeOffset + trackOffsetSeconds) * clockRate);
            int sequenceNumberOffset = this.lastSequenceNumber + 1;
            boolean log = ApplicationProperties.getProperty("RTP_LOGGING") == "true";
            this.state = StreamState.READY;
            this.trackStart = System.currentTimeMillis();
            while (isStarted && generator != null && generator.hasNext()) {
                this.state = StreamState.PLAYING;
                packet = generator.next();
                long trackTime = (System.currentTimeMillis() - trackStart);
                float trackTimeInSecs = trackTime / 1000f;
                float currentTimestampInSecs = (packet.getTimestamp() / (float) clockRate);
                long currentTimestamp = (long) (currentTimestampInSecs * 1000);
                lastSequenceNumber = packet.getSequenceNumber() + sequenceNumberOffset;
                packet = RTPPacket.builder(packet)
                        .sequenceNumber(lastSequenceNumber)
                        .timestamp(packet.getTimestamp() + timestampOffset)
                        .build();
                if (currentTimestamp - trackTime > 0) {
                    try {
                        Thread.sleep(currentTimestamp - trackTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (log) {
                    System.out.println(streamType + " " + trackTimeInSecs + " "
                            + ((currentTimestamp - trackTime) / 1000f) + " " + clockRate + "\r\n"
                            + packet.toString(clockRate));
                }
                if (this.isTerminated) {
                    break;
                }
                socketManager.writeData(packet.toNetworkData());
            }
            this.finished = true;
            this.state = StreamState.FINISHED;
            for (MediaListener listener : listeners) {
                listener.onMediaFinished(this, generator.getTrackInfo());
            }


            socketManager.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Packet size: " + packet.getSize());
            return;
        }
    }


}
