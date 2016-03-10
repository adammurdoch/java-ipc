package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public interface Serializer {
    void writeString(String string) throws IOException;

    void flush() throws IOException;
}
