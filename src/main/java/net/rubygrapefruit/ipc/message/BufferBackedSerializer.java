package net.rubygrapefruit.ipc.message;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class BufferBackedSerializer implements Serializer {
    private final ByteBuffer buffer;

    public BufferBackedSerializer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void writeString(String string) throws IOException {
        byte[] bytes = string.getBytes();
        writeInt(bytes.length);
        writeBytes(bytes);
    }

    private void writeBytes(byte[] bytes) throws IOException {
        int pos = 0;
        while (pos < bytes.length) {
            if (buffer.remaining() == 0) {
                writeToDestination();
            }
            int count = Math.min(bytes.length - pos, buffer.remaining());
            buffer.put(bytes, pos, count);
            pos += count;
        }
    }

    private void writeInt(int value) throws IOException {
        ensure(4);
        buffer.putInt(value);
    }

    private void ensure(int count) throws IOException {
        if (buffer.remaining() < count) {
            writeToDestination();
        }
    }

    private void writeToDestination() throws IOException {
        buffer.flip();
        while (buffer.hasRemaining()) {
            write(buffer);
        }
        buffer.clear();
    }

    /**
     * Write full contents of buffer, update buffer position.
     */
    protected abstract void write(ByteBuffer buffer) throws IOException;

    @Override
    public void flush() throws IOException {
        writeToDestination();
    }
}
