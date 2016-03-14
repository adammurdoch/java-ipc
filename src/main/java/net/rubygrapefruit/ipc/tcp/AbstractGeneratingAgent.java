package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.GeneratingAgent;
import net.rubygrapefruit.ipc.message.Generator;
import net.rubygrapefruit.ipc.message.Receiver;

public abstract class AbstractGeneratingAgent extends AbstractAgent implements GeneratingAgent {
    protected Generator generator;
    protected Receiver receiver;

    @Override
    public void generateFrom(Generator generator) {
        this.generator = generator;
    }

    @Override
    public void receiveTo(Receiver receiver) {
        this.receiver = receiver;
    }
}
