package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public interface Server {
    void start() throws IOException;

    String getConfig();

    void generateFrom(Generator generator);

    void receiveTo(Receiver receiver);

    void stop() throws Exception;
}
