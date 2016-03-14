package net.rubygrapefruit.ipc.message;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ChannelBackedDeserializer implements Deserializer {
    private final ByteChannel channel;
    private final ByteBuffer buffer;

    public ChannelBackedDeserializer(ByteChannel channel) {
        this.channel = channel;
        buffer = ByteBuffer.allocateDirect(1024);
        buffer.limit(0);
    }

    @Override
    public String readString() throws IOException {
        int length = readInt();
        byte[] bytes = new byte[length];
        readBytes(bytes);
        return new String(bytes);
    }

    private void readBytes(byte[] bytes) throws IOException {
        int pos = 0;
        while (pos < bytes.length) {
            ensureBufferNotEmpty();
            int count = Math.min(bytes.length - pos, buffer.remaining());
            buffer.get(bytes, pos, count);
            pos += count;
        }
    }

    private int readInt() throws IOException {
        byte b1 = next();
        byte b2 = next();
        byte b3 = next();
        byte b4 = next();
        return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
    }

    private byte next() throws IOException {
        ensureBufferNotEmpty();
        return buffer.get();
    }

    private void ensureBufferNotEmpty() throws IOException {
        while (buffer.remaining() == 0) {
            buffer.clear();
            if (channel.read(buffer) == -1) {
                throw new EOFException();
            }
            buffer.flip();
        }
    }
}
