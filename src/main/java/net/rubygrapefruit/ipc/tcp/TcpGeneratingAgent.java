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
                executorService.execute(() -> {
                    try {
                        worker(deserializer, serializer, receiver);
                    } catch (IOException e) {
                        throw new RuntimeException("Failure in worker thread.", e);
                    }
                });
                DispatchImpl dispatch = new DispatchImpl(serializer);
                generator.generate(dispatch);
                System.out.println("* Generated " + dispatch.writeCount + " messages.");
            } catch (IOException e) {
                throw new RuntimeException("Failure in worker thread.", e);
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
            super.stop();
            if (clientConnection != null) {
                clientConnection.close();
            }
        } finally {
            serverSocket = null;
            executorService = null;
        }
    }

    private static class DispatchImpl implements Dispatch {
        private final Serializer serializer;
        int writeCount;

        public DispatchImpl(Serializer serializer) {
            this.serializer = serializer;
            writeCount = 0;
        }

        @Override
        public void send(Message message) throws IOException {
            Message.write(message, serializer);
            writeCount++;
            serializer.flush();
        }
    }
}
