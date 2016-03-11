package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public interface ReceivingAgent {
    void setConfig(String config);

    void receiveTo(Receiver receiver);

    void start() throws IOException;

    void waitForCompletion() throws IOException;
}
