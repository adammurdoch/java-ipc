package net.rubygrapefruit.ipc.message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ChannelBackedSerializer implements Serializer {
    private final ByteChannel channel;
    private final ByteBuffer buffer;

    public ChannelBackedSerializer(ByteChannel channel) {
        this.channel = channel;
        buffer = ByteBuffer.allocateDirect(4096);
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
                writeToChannel();
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
            writeToChannel();
        }
    }

    private void writeToChannel() throws IOException {
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        buffer.clear();
    }

    @Override
    public void flush() throws IOException {
        writeToChannel();
    }
}
