package net.rubygrapefruit.ipc.agent;

import net.rubygrapefruit.ipc.message.Receiver;

public abstract class AbstractReceivingAgent extends AbstractAgent implements ReceivingAgent {
    protected Receiver receiver;

    @Override
    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
    }
}
