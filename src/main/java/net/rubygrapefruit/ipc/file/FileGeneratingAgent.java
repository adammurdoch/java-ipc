package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.GeneratingAgent;
import net.rubygrapefruit.ipc.message.Generator;
import net.rubygrapefruit.ipc.message.Receiver;
import net.rubygrapefruit.ipc.tcp.Agent;

import java.io.File;
import java.io.IOException;

public class FileGeneratingAgent extends Agent implements GeneratingAgent {
    private File send;
    private File receive;
    private Generator generator;
    private Receiver receiver;

    @Override
    public void generateFrom(Generator generator) {
        this.generator = generator;
    }

    @Override
    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void start() throws IOException {
        super.start();

        send = File.createTempFile("to-worker", ".bin");
        send.deleteOnExit();
        receive = File.createTempFile("from-worker", ".bin");
        receive.deleteOnExit();
        System.out.println("send on: " + send);
        System.out.println("receive on: " + receive);
        // set up initial file
        new MemoryMappedFileBackedSerializer(receive).close();
        executorService.execute(() -> {
            try {
                try (MemoryMappedFileBackedSerializer serializer = new MemoryMappedFileBackedSerializer(send)) {
                    generatorLoop(serializer, generator);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failure in generator thread.", e);
            }
        });
        executorService.execute(() -> {
            try {
                try (MemoryMappedFileBackedDeserializer deserializer = new MemoryMappedFileBackedDeserializer(receive)) {
                    receiverLoop(deserializer, noSend(), receiver);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failure in receiver thread.", e);
            }
        });
    }

    @Override
    public String getConfig() {
        return receive.getAbsolutePath() + File.pathSeparator + send.getAbsolutePath();
    }

    @Override
    public void waitForCompletion() throws Exception {
        waitForThreads();
    }
}
