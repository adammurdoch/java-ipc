package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Agent {
    protected void writeTo(Message message, DataOutputStream outputStream) throws IOException {
        byte[] bytes = message.text.getBytes();
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);
    }

    protected void worker(Deserializer deserializer, final Serializer serializer, Receiver receiver)
            throws IOException {
        AtomicBoolean done = new AtomicBoolean();
        while (!done.get()) {
            Message message = Message.read(deserializer);
            receiver.receive(message, new ReceiveContext() {
                @Override
                public void done() {
                    done.set(true);
                }

                @Override
                public void send(Message message) throws IOException {
                    Message.write(message, serializer);
                }
            });
        }
    }
}
