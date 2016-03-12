package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class Agent {
    protected ExecutorService executorService;

    protected void startReceiverLoop(Serializer serializer, Deserializer deserializer, Receiver receiver) {
        executorService.execute(() -> {
            try {
                receiverLoop(deserializer, serializer, receiver);
            } catch (IOException e) {
                throw new RuntimeException("Failure in worker thread.", e);
            }
        });
    }

    protected void receiverLoop(Deserializer deserializer, Serializer serializer, Receiver receiver) throws IOException {
        int readCound = 0;
        ReceiveContextImpl context = new ReceiveContextImpl(serializer);
        while (!context.done) {
            Message message = Message.read(deserializer);
            readCound++;
            receiver.receive(message, context);
        }
        System.out.println("* Receiver handled " + readCound + " messages, sent " + context.writeCount + " messages.");
    }

    protected void generatorLoop(Serializer serializer, Generator generator) throws IOException {
        SerializerBackedDispatch dispatch = new SerializerBackedDispatch(serializer);
        generator.generate(dispatch);
        System.out.println("* Generated " + dispatch.writeCount + " messages.");
    }

    protected void start() throws IOException {
        executorService = Executors.newCachedThreadPool();
    }

    protected void stop() throws Exception {
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for completion.");
        }
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
