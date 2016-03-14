package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.agent.AbstractReceivingAgent;
import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;

public abstract class AbstractTcpReceivingAgent extends AbstractReceivingAgent {
    private int port;
    private Connection connection;

    @Override
    public void setConfig(String config) {
        port = Integer.valueOf(config);
    }

    @Override
    public void start() throws IOException {
        connection = createConnection(port);
    }

    protected abstract Connection createConnection(int port) throws IOException;

    @Override
    public void waitForCompletion() throws IOException {
        Deserializer deserializer = connection.getReceive();
        Serializer serializer = connection.getSend();
        receiverLoop(deserializer, serializer, receiver);
        try {
            connection.close();
        } finally {
            connection = null;
        }
    }
}
