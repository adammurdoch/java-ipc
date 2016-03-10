package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.Dispatch;
import net.rubygrapefruit.ipc.Generator;
import net.rubygrapefruit.ipc.Message;
import net.rubygrapefruit.ipc.Receiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
                DataOutputStream outputStream = new DataOutputStream(clientConnection.getOutputStream());
                DataInputStream inputStream = new DataInputStream(clientConnection.getInputStream());
                executorService.execute(() -> {
                    try {
                        worker(inputStream, outputStream, receiver);
                    } catch (IOException e) {
                        throw new RuntimeException("Failure in worker thread.", e);
                    }
                });
                generator.generate(new Dispatch() {
                    @Override
                    public void send(Message message) throws IOException {
                        writeTo(message, outputStream);
                        outputStream.flush();
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
