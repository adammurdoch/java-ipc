package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TcpServer extends Agent {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private Generator generator;
    private Receiver receiver;
    private Socket clientConnection;

    public void start() throws IOException {
        serverSocket = new ServerSocket(0);
        executorService = Executors.newCachedThreadPool();
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
                generator.generate(new Dispatch() {
                    @Override
                    public void send(Message message) throws IOException {
                        Message.write(message, serializer);
                        serializer.flush();
                    }
                });
            } catch (IOException e) {
                throw new RuntimeException("Failure in worker thread.", e);
            }
        });
    }

    public String getConfig() {
        return String.valueOf(serverSocket.getLocalPort());
    }

    public void generateFrom(Generator generator) {
        this.generator = generator;
    }

    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
    }

    public void stop() throws Exception {
        try {
            serverSocket.close();
            executorService.shutdown();
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("Timeout waiting for completion.");
            }
            if (clientConnection != null) {
                clientConnection.close();
            }
        } finally {
            serverSocket = null;
            executorService = null;
        }
    }
}
