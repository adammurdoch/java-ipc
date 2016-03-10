package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public interface Dispatch {
    void send(Message message) throws IOException;
}
