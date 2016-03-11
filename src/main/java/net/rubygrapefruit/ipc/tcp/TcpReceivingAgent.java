package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TcpReceivingAgent extends Agent implements ReceivingAgent {
    private int port;
    private Socket socket;
    private Receiver receiver;

    @Override
    public void setConfig(String config) {
        port = Integer.valueOf(config);
    }

    @Override
    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void start() throws IOException {
        socket = new Socket(InetAddress.getLoopbackAddress(), port);
    }

    @Override
    public void waitForCompletion() throws IOException {
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
