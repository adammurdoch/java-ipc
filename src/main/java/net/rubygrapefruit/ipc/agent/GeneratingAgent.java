package net.rubygrapefruit.ipc.agent;

import net.rubygrapefruit.ipc.message.Generator;
import net.rubygrapefruit.ipc.message.Receiver;

import java.io.IOException;

public interface GeneratingAgent {
    void start(FlushStrategy flushStrategy) throws IOException;

    String getConfig() throws IOException;

    void generateFrom(Generator generator);

    void receiveTo(Receiver receiver);

    void waitForCompletion() throws Exception;
}
