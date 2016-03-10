package net.rubygrapefruit.ipc.message;

import java.io.IOException;

public class Message {
    public final String text;

    public Message(String text) {
        this.text = text;
    }

    public static void write(Message message, Serializer serializer) throws IOException {
        serializer.writeString(message.text);
    }

    public static Message read(Deserializer deserializer) throws IOException {
        return new Message(deserializer.readString());
    }
}
