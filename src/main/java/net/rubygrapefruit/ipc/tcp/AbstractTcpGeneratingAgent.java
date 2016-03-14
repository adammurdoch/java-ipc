package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.agent.AbstractGeneratingAgent;
import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractTcpGeneratingAgent extends AbstractGeneratingAgent {
    private ServerSocket serverSocket;
    private Socket clientConnection;

    @Override
    public void start() throws IOException {
        super.start();
        serverSocket = new ServerSocket(0);
        executorService.execute(() -> {
            try {
                clientConnection = serverSocket.accept();
                System.out.println("* Connected");
                Serializer serializer = createSerializer(clientConnection);
                Deserializer deserializer = createDeserializer(clientConnection);
                startReceiverLoop(noSend(), deserializer, receiver);
                generatorLoop(serializer, generator);
            } catch (IOException e) {
                throw new RuntimeException("Failure in generator thread.", e);
            }
        });
    }

    protected abstract Serializer createSerializer(Socket connection) throws IOException;

    protected abstract Deserializer createDeserializer(Socket connection) throws IOException;

    @Override
    public String getConfig() {
        return String.valueOf(serverSocket.getLocalPort());
    }

    @Override
    public void waitForCompletion() throws Exception {
        try {
            serverSocket.close();
            waitForThreads();
            if (clientConnection != null) {
                clientConnection.close();
            }
        } finally {
            serverSocket = null;
            executorService = null;
        }
    }
}
