package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.Receiver;
import net.rubygrapefruit.ipc.message.ReceivingAgent;
import net.rubygrapefruit.ipc.tcp.Agent;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class FileReceivingAgent extends Agent implements ReceivingAgent {
    @Override
    public void receiveTo(Receiver receiver) {

    }

    @Override
    public void setConfig(String config) {
        String[] paths = config.split(Pattern.quote(File.pathSeparator));
        File send = new File(paths[0]);
        File receive = new File(paths[1]);
        System.out.println("worker send on: " + send);
        System.out.println("worker receive on: " + receive);
    }

    @Override
    public void start() throws IOException {

    }

    @Override
    public void waitForCompletion() throws IOException {

    }
}
