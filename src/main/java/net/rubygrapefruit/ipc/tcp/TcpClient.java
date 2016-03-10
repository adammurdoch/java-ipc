package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.Receiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient extends Agent {
    private int port;
    private Socket socket;
    private Receiver receiver;

    public void setConfig(String config) {
        port = Integer.valueOf(config);
    }

    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
    }

    public void start() throws IOException {
        socket = new Socket(InetAddress.getLoopbackAddress(), port);
    }

    public void stop() throws IOException {
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        worker(inputStream, outputStream, receiver);
        try {
            socket.close();
        } finally {
            socket = null;
        }
    }
}
