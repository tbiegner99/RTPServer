package com.rtp.stream.socket;

import java.io.IOException;

public interface SocketManager {
    int getLocalPort();

    void writeData(byte[] buffer) throws IOException;

    void close();
}
