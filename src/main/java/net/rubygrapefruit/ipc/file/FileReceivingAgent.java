package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.agent.AbstractReceivingAgent;
import net.rubygrapefruit.ipc.agent.FlushStrategy;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileReceivingAgent extends AbstractReceivingAgent {
    private final boolean unsafe;
    private File send;
    private File receive;

    public FileReceivingAgent(boolean unsafe) {
        this.unsafe = unsafe;
    }

    @Override
    public void setConfig(String config, FlushStrategy flushStrategy) {
        setFlushStrategy(flushStrategy);
        String[] paths = config.split(Pattern.quote(File.pathSeparator));
        send = new File(paths[0]);
        receive = new File(paths[1]);
        System.out.println("worker send on: " + send);
        System.out.println("worker receive on: " + receive);
    }

    @Override
    public void start() throws IOException {
        try (MappedByteBufferBackedSerializer serializer = unsafe
                ? new UnsafeMemoryMappedFileBackedSerializer(send)
                : new MemoryMappedFileBackedSerializer(send);
             MappedByteBufferBackedDeserializer deserializer = unsafe
                     ? new UnsafeMemoryMappedFileBackedDeserializer(receive)
                     : new MemoryMappedFileBackedDeserializer(receive)) {
            receiverLoop(deserializer, serializer, receiver);
        }
    }

    @Override
    public void waitForCompletion() throws IOException {
    }
}
