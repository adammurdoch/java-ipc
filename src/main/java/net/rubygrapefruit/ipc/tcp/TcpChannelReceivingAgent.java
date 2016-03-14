package net.rubygrapefruit.ipc.tcp;

import java.io.IOException;

public class TcpChannelReceivingAgent extends AbstractTcpReceivingAgent {
    @Override
    protected Connection createConnection(int port) throws IOException {
        return new TcpChannelConnection(port);
    }
}
