package net.rubygrapefruit.ipc.tcp;

import java.io.IOException;

public class TcpChannelGeneratingAgent extends AbstractTcpGeneratingAgent {
    @Override
    protected IncomingConnection createIncomingConnection() throws IOException {
        return new TcpChannelIncomingConnection();
    }
}