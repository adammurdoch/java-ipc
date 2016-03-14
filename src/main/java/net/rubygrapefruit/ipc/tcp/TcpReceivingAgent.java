package net.rubygrapefruit.ipc.tcp;

import java.io.IOException;

public class TcpReceivingAgent extends AbstractTcpReceivingAgent {
    private final boolean buffered;

    public TcpReceivingAgent(boolean buffered) {
        this.buffered = buffered;
    }

    @Override
    protected Connection createConnection(int port) throws IOException {
        return new TcpStreamConnection(port, buffered);
    }
}
