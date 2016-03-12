package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.Receiver;
import net.rubygrapefruit.ipc.message.ReceivingAgent;
import net.rubygrapefruit.ipc.tcp.Agent;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileReceivingAgent extends Agent implements ReceivingAgent {
    private Receiver receiver;
    private File send;
    private File receive;

    @Override
    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void setConfig(String config) {
        String[] paths = config.split(Pattern.quote(File.pathSeparator));
        send = new File(paths[0]);
        receive = new File(paths[1]);
        System.out.println("worker send on: " + send);
        System.out.println("worker receive on: " + receive);
    }

    @Override
    public void start() throws IOException {
        try (MemoryMappedFileBackedSerializer serializer = new MemoryMappedFileBackedSerializer(send); MemoryMappedFileBackedDeserializer deserializer = new MemoryMappedFileBackedDeserializer(receive)) {
            receiverLoop(deserializer, serializer, receiver);
        }
    }

    @Override
    public void waitForCompletion() throws IOException {

    }
}
