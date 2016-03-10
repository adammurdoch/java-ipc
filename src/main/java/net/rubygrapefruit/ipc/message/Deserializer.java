package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public interface Deserializer {
    String readString() throws IOException;
}
