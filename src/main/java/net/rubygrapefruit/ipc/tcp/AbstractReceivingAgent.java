package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.Receiver;
import net.rubygrapefruit.ipc.message.ReceivingAgent;

public abstract class AbstractReceivingAgent extends AbstractAgent implements ReceivingAgent {
    protected Receiver receiver;

    @Override
    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
    }
}
