package net.rubygrapefruit.ipc.agent;

import net.rubygrapefruit.ipc.message.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class AbstractAgent {
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
        serializer.flush();
        System.out.println("* Receiver handled " + readCound + " messages, sent " + context.writeCount + " messages.");
    }

    protected void generatorLoop(Serializer serializer, Generator generator) throws IOException {
        SerializerBackedDispatch dispatch = new SerializerBackedDispatch(serializer);
        generator.generate(dispatch);
        serializer.flush();
        System.out.println("* Generated " + dispatch.writeCount + " messages.");
    }

    public void start() throws IOException {
        executorService = Executors.newCachedThreadPool();
    }

    protected void waitForThreads() throws Exception {
        executorService.shutdown();
        if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
            throw new RuntimeException("Timeout waiting for completion.");
        }
    }

    protected Serializer noSend() {
        return new Serializer() {
            @Override
            public void writeString(String string) throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void flush() throws IOException {
            }
        };
    }

    private static class ReceiveContextImpl extends SerializerBackedDispatch implements ReceiveContext {
        boolean done;

        public ReceiveContextImpl(Serializer serializer) {
            super(serializer);
        }

        @Override
        public void done() {
            done = true;
        }
    }
}
