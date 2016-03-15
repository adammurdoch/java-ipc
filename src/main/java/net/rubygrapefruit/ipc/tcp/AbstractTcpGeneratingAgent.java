package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.agent.AbstractGeneratingAgent;
import net.rubygrapefruit.ipc.agent.FlushStrategy;
import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;

public abstract class AbstractTcpGeneratingAgent extends AbstractGeneratingAgent {
    private IncomingConnection incomingConnection;
    private Connection clientConnection;

    @Override
    public void start(FlushStrategy flushStrategy) throws IOException {
        setFlushStrategy(flushStrategy);
        incomingConnection = createIncomingConnection();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    clientConnection = incomingConnection.accept();
                    System.out.println("* Connected");
                    Serializer serializer = clientConnection.getSend();
                    Deserializer deserializer = clientConnection.getReceive();
                    startReceiverLoop(noSend(), deserializer, receiver);
                    generatorLoop(serializer, generator);
                } catch (IOException e) {
                    throw new RuntimeException("Failure in generator thread.", e);
                }
            }});
    }

    protected abstract IncomingConnection createIncomingConnection() throws IOException;

    @Override
    public String getConfig() throws IOException {
        return String.valueOf(incomingConnection.getPort());
    }

    @Override
    public void waitForCompletion() throws Exception {
        try {
            incomingConnection.close();
            waitForThreads();
            if (clientConnection != null) {
                clientConnection.close();
            }
        } finally {
            incomingConnection = null;
        }
    }
}
