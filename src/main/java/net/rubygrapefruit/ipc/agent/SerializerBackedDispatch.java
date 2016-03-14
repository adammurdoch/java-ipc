package net.rubygrapefruit.ipc.agent;

import net.rubygrapefruit.ipc.message.Dispatch;
import net.rubygrapefruit.ipc.message.Message;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;

public class SerializerBackedDispatch implements Dispatch {
    private final Serializer serializer;
    int writeCount;

    public SerializerBackedDispatch(Serializer serializer) {
        this.serializer = serializer;
        writeCount = 0;
    }

    @Override
    public void send(Message message) throws IOException {
        Message.write(message, serializer);
        writeCount++;
    }
}
