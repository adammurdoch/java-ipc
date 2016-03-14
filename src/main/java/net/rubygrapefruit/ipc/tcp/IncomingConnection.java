package net.rubygrapefruit.ipc.tcp;

import java.io.Closeable;
import java.io.IOException;

public interface IncomingConnection extends Closeable {
    int getPort() throws IOException;

    Connection accept() throws IOException;
}
