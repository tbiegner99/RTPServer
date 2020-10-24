package com.rtp.stream;

import com.rtp.packet.RTPPacketGenerator;
import com.rtp.stream.socket.SocketManager;
import com.tj.mp4.MediaInfo;

public interface MediaStream extends Stream {
    void setupMedia(RTPPacketGenerator generator) throws IllegalStateException;

    RTPPacketGenerator getCurrentMedia() throws IllegalStateException;

    MediaInfo getMediaInfo();

    SocketManager getSocketManager();

    StreamType getType();

    int getClockTicksAtTime(float timeInSeconds);

    int getLastSequenceNumber();

    long getRealStartTime();
}
