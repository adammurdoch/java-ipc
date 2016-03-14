package net.rubygrapefruit.ipc.message;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BufferingInputStreamBackedDeserializer extends BufferBackedDeserializer {
    private final InputStream inputStream;

    public BufferingInputStreamBackedDeserializer(InputStream inputStream) {
        super(ByteBuffer.allocate(1024));
        this.inputStream = inputStream;
    }

    @Override
    protected int read(ByteBuffer buffer) throws IOException {
        int nread = inputStream.read(buffer.array(), buffer.arrayOffset() + buffer.position(), buffer.remaining());
        if (nread > 0) {
            buffer.position(buffer.position() + nread);
        }
        return nread;
    }
}
