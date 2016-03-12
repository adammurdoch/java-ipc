package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpGeneratingAgent extends Agent implements GeneratingAgent {
    private ServerSocket serverSocket;
    private Generator generator;
    private Receiver receiver;
    private Socket clientConnection;

    @Override
    public void start() throws IOException {
        super.start();
        serverSocket = new ServerSocket(0);
        executorService.execute(() -> {
            try {
                clientConnection = serverSocket.accept();
                System.out.println("* Connected");
                Serializer serializer = new OutputStreamBackedSerializer(clientConnection.getOutputStream());
                Deserializer deserializer = new InputStreamBackedDeserializer(clientConnection.getInputStream());
                startReceiverLoop(serializer, deserializer, receiver);
                generatorLoop(serializer, generator);
            } catch (IOException e) {
                throw new RuntimeException("Failure in generator thread.", e);
            }
        });
    }

    @Override
    public String getConfig() {
        return String.valueOf(serverSocket.getLocalPort());
    }

    @Override
    public void generateFrom(Generator generator) {
        this.generator = generator;
    }

    @Override
    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
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
