package net.rubygrapefruit.ipc.message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

public class ChannelBackedSerializer extends BufferBackedSerializer {
    private final ByteChannel channel;

    public ChannelBackedSerializer(ByteChannel channel) {
        super(ByteBuffer.allocateDirect(1024));
        this.channel = channel;
    }

    @Override
    protected void write(ByteBuffer buffer) throws IOException {
        channel.write(buffer);
    }
}
