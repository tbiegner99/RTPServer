package com.rtp.stream.socket;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.*;

public class UnicastSocketManager implements SocketManager {

    private DatagramSocket socket;
    private InetAddress clientAddress;
    private int clientPort;

    public UnicastSocketManager(InetAddress clientAddress, int clientPort) throws IOException {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        socket=new DatagramSocket();
    }

    @Override
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    @Override
    public void writeData(byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer,buffer.length,clientAddress,clientPort);
        socket.send(packet);
    }

    //public boolean

    @Override
    public void close() {
        //socket.close();
    }
}
