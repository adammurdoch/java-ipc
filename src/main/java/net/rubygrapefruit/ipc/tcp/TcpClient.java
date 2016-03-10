package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.*;

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
        Deserializer deserializer = new InputStreamBackedDeserializer(socket.getInputStream());
        Serializer serializer = new OutputStreamBackedSerializer(socket.getOutputStream());
        worker(deserializer, serializer, receiver);
        try {
            socket.close();
        } finally {
            socket = null;
        }
    }
}
