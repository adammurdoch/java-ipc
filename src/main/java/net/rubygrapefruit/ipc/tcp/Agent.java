package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.Message;
import net.rubygrapefruit.ipc.ReceiveContext;
import net.rubygrapefruit.ipc.Receiver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class Agent {
    protected void writeTo(Message message, DataOutputStream outputStream) throws IOException {
        byte[] bytes = message.text.getBytes();
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);
    }

    protected void worker(DataInputStream inputStream, final DataOutputStream outputStream, Receiver receiver)
            throws IOException {
        AtomicBoolean done = new AtomicBoolean();
        while (!done.get()) {
            int length = inputStream.readInt();
            byte[] bytes = new byte[length];
            inputStream.readFully(bytes);
            receiver.receive(new Message(new String(bytes)), new ReceiveContext() {
                @Override
                public void done() {
                    done.set(true);
                }

                @Override
                public void send(Message message) throws IOException {
                    writeTo(message, outputStream);
                }
            });
        }
    }
}
