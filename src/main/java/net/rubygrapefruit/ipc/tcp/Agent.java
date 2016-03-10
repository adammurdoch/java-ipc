package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.*;

import java.io.IOException;

public class Agent {
    protected void worker(Deserializer deserializer, final Serializer serializer, Receiver receiver)
            throws IOException {
        int readCound = 0;
        ReceiveContextImpl context = new ReceiveContextImpl(serializer);
        while (!context.done) {
            Message message = Message.read(deserializer);
            readCound++;
            receiver.receive(message, context);
        }
        System.out.println("* Receiver handled " + readCound + " messages, sent " + context.writeCount + " messages.");
    }

    private static class ReceiveContextImpl implements ReceiveContext {
        private final Serializer serializer;
        int writeCount;
        boolean done;

        public ReceiveContextImpl(Serializer serializer) {
            this.serializer = serializer;
            writeCount = 0;
        }

        @Override
        public void done() {
            done = true;
        }

        @Override
        public void send(Message message) throws IOException {
            Message.write(message, serializer);
            writeCount++;
        }
    }
}
