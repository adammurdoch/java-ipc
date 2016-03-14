package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.agent.AbstractReceivingAgent;
import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public abstract class AbstractTcpReceivingAgent extends AbstractReceivingAgent {
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

    protected abstract Serializer createSerializer(Socket connection) throws IOException;

    protected abstract Deserializer createDeserializer(Socket connection) throws IOException;


    @Override
    public void waitForCompletion() throws IOException {
        Deserializer deserializer = createDeserializer(socket);
        Serializer serializer = createSerializer(socket);
        receiverLoop(deserializer, serializer, receiver);
        try {
            socket.close();
        } finally {
            socket = null;
        }
    }
}
