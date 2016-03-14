package net.rubygrapefruit.ipc.message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamBackedSerializer implements Serializer {
    private final DataOutputStream outputStream;

    public OutputStreamBackedSerializer(OutputStream outputStream) {
        this.outputStream = new DataOutputStream(outputStream);
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void writeString(String string) throws IOException {
        byte[] bytes = string.getBytes();
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);
    }
}
