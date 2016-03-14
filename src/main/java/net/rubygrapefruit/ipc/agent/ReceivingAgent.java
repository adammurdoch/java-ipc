package net.rubygrapefruit.ipc.agent;

import net.rubygrapefruit.ipc.message.Receiver;

import java.io.IOException;

public interface ReceivingAgent {
    void setConfig(String config);

    void receiveTo(Receiver receiver);

    void start() throws IOException;

    void waitForCompletion() throws IOException;
}
