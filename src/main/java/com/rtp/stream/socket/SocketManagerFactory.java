package com.rtp.stream.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

public class SocketManagerFactory {

    public static Builder buildSocketManager() {
        return new Builder();
    }

    public static class Builder {
        private Integer clientPort;
        private StreamSocketType socketType;
        private InetAddress clientAddress;

        public Builder withSocketType(StreamSocketType socketType) {
            this.socketType=socketType;
            return this;
        }
        public Builder withClientAddress(InetAddress clientAddress) {
            this.clientAddress=clientAddress;
            return this;
        }

        public Builder withClientPort(Integer clientPort) {
            this.clientPort=clientPort;
            return this;
        }

        public SocketManager build() throws IOException {
            switch(socketType) {
                case UNICAST: return new UnicastSocketManager(clientAddress,clientPort);
                default: throw new UnsupportedOperationException("Socket type not supported: "+socketType.name());
            }
        }
    }
}
