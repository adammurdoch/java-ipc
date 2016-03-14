package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.ChannelBackedDeserializer;
import net.rubygrapefruit.ipc.message.ChannelBackedSerializer;
import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TcpChannelConnection implements Connection {
    private final SocketChannel channel;
    private final ChannelBackedDeserializer receive;
    private final ChannelBackedSerializer send;

    public TcpChannelConnection(SocketChannel channel) {
        this.channel = channel;
        this.receive = new ChannelBackedDeserializer(channel);
        this.send = new ChannelBackedSerializer(channel);
    }

    public TcpChannelConnection(int port) throws IOException {
        this(SocketChannel.open(new InetSocketAddress(InetAddress.getLoopbackAddress(), port)));
    }

    @Override
    public Deserializer getReceive() {
        return receive;
    }

    @Override
    public Serializer getSend() {
        return send;
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
