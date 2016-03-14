package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.ChannelBackedDeserializer;
import net.rubygrapefruit.ipc.message.ChannelBackedSerializer;
import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;
import java.net.Socket;

public class TcpChannelGeneratingAgent extends AbstractTcpGeneratingAgent {
    @Override
    protected Deserializer createDeserializer(Socket connection) throws IOException {
        return new ChannelBackedDeserializer(connection.getChannel());
    }

    @Override
    protected Serializer createSerializer(Socket connection) throws IOException {
        return new ChannelBackedSerializer(connection.getChannel());
    }
}
