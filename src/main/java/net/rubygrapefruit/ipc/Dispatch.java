package net.rubygrapefruit.ipc;

import java.io.IOException;

public interface Dispatch {
    void send(Message message) throws IOException;
}
