package net.rubygrapefruit.ipc.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

public class TcpChannelIncomingConnection implements IncomingConnection {
    private final ServerSocketChannel serverChannel;

    public TcpChannelIncomingConnection() throws IOException {
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(0));
    }

    @Override
    public int getPort() throws IOException {
        return ((InetSocketAddress) serverChannel.getLocalAddress()).getPort();
    }

    @Override
    public Connection accept() throws IOException {
        return new TcpChannelConnection(serverChannel.accept());
    }

    @Override
    public void close() throws IOException {
        serverChannel.close();
    }
}
