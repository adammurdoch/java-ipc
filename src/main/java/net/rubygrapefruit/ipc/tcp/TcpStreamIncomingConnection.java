package net.rubygrapefruit.ipc.tcp;

import java.io.IOException;
import java.net.ServerSocket;

public class TcpStreamIncomingConnection implements IncomingConnection {
    private final ServerSocket serverSocket;
    private final boolean buffered;

    public TcpStreamIncomingConnection(boolean buffered) throws IOException {
        this.buffered = buffered;
        serverSocket = new ServerSocket(0);
    }

    @Override
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    @Override
    public Connection accept() throws IOException {
        return new TcpStreamConnection(serverSocket.accept(), buffered);
    }

    @Override
    public void close() throws IOException {
        serverSocket.close();
    }
}
