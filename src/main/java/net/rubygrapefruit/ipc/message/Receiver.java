package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public interface Receiver {
    void receive(Message message, ReceiveContext context) throws IOException;
}
