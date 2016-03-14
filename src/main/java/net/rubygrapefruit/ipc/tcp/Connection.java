package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.Closeable;

public interface Connection extends Closeable {
    Serializer getSend();

    Deserializer getReceive();
}
