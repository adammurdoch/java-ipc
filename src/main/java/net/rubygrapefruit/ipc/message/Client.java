package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public interface Client {
    void setConfig(String config);

    void receiveTo(Receiver receiver);

    void start() throws IOException;

    void stop() throws IOException;
}
