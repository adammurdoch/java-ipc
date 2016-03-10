package net.rubygrapefruit.ipc;

import net.rubygrapefruit.ipc.message.Message;
import net.rubygrapefruit.ipc.message.GeneratingAgent;
import net.rubygrapefruit.ipc.tcp.TcpGeneratingAgent;
import net.rubygrapefruit.ipc.worker.WorkerMain;

import java.io.File;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("* Starting agent");
        GeneratingAgent agent = new TcpGeneratingAgent();
        agent.generateFrom(dispatch -> {
            for (int i = 0; i < 20000; i++) {
                dispatch.send(new Message(String.valueOf(i)));
            }
            dispatch.send(new Message("done"));
        });
        agent.receiveTo((message, context) -> {
            if (message.text.equals("done")) {
                context.done();
            }
        });
        agent.start();

        System.out.println("* Starting worker");
        URL codeSource = Main.class.getProtectionDomain().getCodeSource().getLocation();
        if (!codeSource.getProtocol().equals("file")) {
            throw new RuntimeException("Cannot calculate classpath from " + codeSource);
        }
        File classPath = new File(codeSource.toURI());
        ProcessBuilder processBuilder = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-cp",
                classPath.getAbsolutePath(), WorkerMain.class.getName(), agent.getConfig());
        processBuilder.inheritIO().start().waitFor();

        System.out.println("* Waiting for completion");
        agent.stop();
    }
}
