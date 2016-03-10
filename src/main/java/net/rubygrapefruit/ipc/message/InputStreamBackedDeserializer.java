package net.rubygrapefruit.ipc.message;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class InputStreamBackedDeserializer implements Deserializer {
    private final DataInputStream inputStream;

    public InputStreamBackedDeserializer(InputStream inputStream) {
        this.inputStream = new DataInputStream(inputStream);
    }

    @Override
    public String readString() throws IOException {
        return inputStream.readUTF();
    }
}
