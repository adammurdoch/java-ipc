package net.rubygrapefruit.ipc.tcp;

import java.io.IOException;

public class TcpGeneratingAgent extends AbstractTcpGeneratingAgent {
    @Override
    protected IncomingConnection createIncomingConnection() throws IOException {
        return new TcpStreamIncomingConnection();
    }
}
