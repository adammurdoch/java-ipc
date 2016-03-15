package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.agent.AbstractGeneratingAgent;
import net.rubygrapefruit.ipc.agent.FlushStrategy;

import java.io.File;
import java.io.IOException;

public class FileGeneratingAgent extends AbstractGeneratingAgent {
    private final boolean unsafe;
    private File send;
    private File receive;

    public FileGeneratingAgent(boolean unsafe) {
        this.unsafe = unsafe;
    }

    @Override
    public void start(FlushStrategy flushStrategy) throws IOException {
        setFlushStrategy(flushStrategy);

        send = File.createTempFile("to-worker", ".bin");
        send.deleteOnExit();
        receive = File.createTempFile("from-worker", ".bin");
        receive.deleteOnExit();
        System.out.println("send on: " + send);
        System.out.println("receive on: " + receive);
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MappedByteBufferBackedSerializer serializer = unsafe
                            ? new UnsafeMemoryMappedFileBackedSerializer(send)
                            : new MemoryMappedFileBackedSerializer(send);
                    try {
                        generatorLoop(serializer, generator);
                    } finally {
                        serializer.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failure in generator thread.", e);
                }
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MappedByteBufferBackedDeserializer deserializer = unsafe
                            ? new UnsafeMemoryMappedFileBackedDeserializer(receive)
                            : new MemoryMappedFileBackedDeserializer(receive);
                    try {
                        receiverLoop(deserializer, noSend(), receiver);
                    } finally {
                        deserializer.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Failure in receiver thread.", e);
                }
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
