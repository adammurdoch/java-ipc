package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.agent.AbstractReceivingAgent;
import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.InputStreamBackedDeserializer;
import net.rubygrapefruit.ipc.message.OutputStreamBackedSerializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TcpReceivingAgent extends AbstractReceivingAgent {
    private int port;
    private Socket socket;

    @Override
    public void setConfig(String config) {
        port = Integer.valueOf(config);
    }

    @Override
    public void start() throws IOException {
        socket = new Socket(InetAddress.getLoopbackAddress(), port);
    }

    @Override
    public void waitForCompletion() throws IOException {
        Deserializer deserializer = new InputStreamBackedDeserializer(socket.getInputStream());
        Serializer serializer = new OutputStreamBackedSerializer(socket.getOutputStream());
        receiverLoop(deserializer, serializer, receiver);
        try {
            socket.close();
        } finally {
            socket = null;
        }
    }
}
