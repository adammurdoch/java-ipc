package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.GeneratingAgent;
import net.rubygrapefruit.ipc.message.Generator;
import net.rubygrapefruit.ipc.message.Receiver;
import net.rubygrapefruit.ipc.tcp.Agent;

import java.io.File;
import java.io.IOException;

public class FileGeneratingAgent extends Agent implements GeneratingAgent {
    private File send;
    private File receive;

    @Override
    public void generateFrom(Generator generator) {

    }

    @Override
    public void receiveTo(Receiver receiver) {

    }

    @Override
    public void start() throws IOException {
        send = File.createTempFile("to-worker", ".bin");
        send.deleteOnExit();
        receive = File.createTempFile("from-worker", ".bin");
        receive.deleteOnExit();
        System.out.println("send on: " + send);
        System.out.println("receive on: " + receive);
    }

    @Override
    public String getConfig() {
        return receive.getAbsolutePath() + File.pathSeparator + send.getAbsolutePath();
    }

    @Override
    public void waitForCompletion() throws Exception {

    }
}
