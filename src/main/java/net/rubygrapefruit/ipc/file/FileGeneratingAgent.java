package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.tcp.AbstractGeneratingAgent;

import java.io.File;
import java.io.IOException;

public class FileGeneratingAgent extends AbstractGeneratingAgent {
    private File send;
    private File receive;

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
