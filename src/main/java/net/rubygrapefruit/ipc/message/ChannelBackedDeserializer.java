package net.rubygrapefruit.ipc.message;

import java.io.IOException;
import java.nio.channels.ByteChannel;

public class ChannelBackedDeserializer implements Deserializer {
    public ChannelBackedDeserializer(ByteChannel channel) {
    }

    @Override
    public String readString() throws IOException {
        throw new UnsupportedOperationException();
    }
}
