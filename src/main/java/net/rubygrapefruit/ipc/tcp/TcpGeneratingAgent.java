package net.rubygrapefruit.ipc.tcp;

import java.io.IOException;

public class TcpGeneratingAgent extends AbstractTcpGeneratingAgent {
    private final boolean buffered;

    public TcpGeneratingAgent(boolean buffered) {
        this.buffered = buffered;
    }

    @Override
    protected IncomingConnection createIncomingConnection() throws IOException {
        return new TcpStreamIncomingConnection(buffered);
    }
}
