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
        throw new UnsupportedOperationException();
    }

    @Override
    public void flush() throws IOException {
        throw new UnsupportedOperationException();
    }
}
