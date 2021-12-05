package com.rtp.stream;

import com.rtp.stream.socket.SocketManager;
import com.rtp.stream.socket.SocketManagerFactory;
import com.rtp.stream.socket.StreamSocketType;
import com.rtp.stream.socket.UnicastSocketManager;

import java.net.InetAddress;
import java.net.SocketException;

public class StreamFactory {

    public static Builder buildMediaStream() {
        return new Builder();
    }

    public static class Builder {
        private StreamType streamType;
        private SocketManager socketManager;
        private int lastSequenceNumber;
        private float startingTimestamp;

        public Builder ofType(StreamType streamType) {
            this.streamType=streamType;
            return this;
        }

        public Builder withSocketManager(SocketManager socketManager) {
            this.socketManager=socketManager;
            return this;
        }

        public Builder withStartingTime(float startingTimestamp) {
            this.startingTimestamp=startingTimestamp;
            return this;
        }

        public Builder withLastSequenceNumber(int lastSequenceNumber) {
            this.lastSequenceNumber =lastSequenceNumber;
            return this;
        }

        public MediaStream build() {
            return new ElementaryStream(streamType,socketManager,lastSequenceNumber,startingTimestamp);
        }


    }
}
