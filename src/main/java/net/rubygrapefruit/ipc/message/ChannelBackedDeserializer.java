package net.rubygrapefruit.ipc.message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ChannelBackedDeserializer extends BufferBackedDeserializer {
    private final ByteChannel channel;

    public ChannelBackedDeserializer(ByteChannel channel) {
        super(ByteBuffer.allocateDirect(1024));
        this.channel = channel;
    }

    @Override
    protected int read(ByteBuffer buffer) throws IOException {
        return channel.read(buffer);
    }
}
