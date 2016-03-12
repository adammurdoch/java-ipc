package net.rubygrapefruit.ipc;

import net.rubygrapefruit.ipc.file.FileGeneratingAgent;
import net.rubygrapefruit.ipc.message.GeneratingAgent;
import net.rubygrapefruit.ipc.message.Message;
import net.rubygrapefruit.ipc.tcp.TcpGeneratingAgent;
import net.rubygrapefruit.ipc.worker.WorkerMain;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws Exception {
        Transport transport = toTransport(args[0]);
        System.out.println("* Transport: " + transport);

        System.out.println("* Starting generator");
        GeneratingAgent agent = createAgent(transport);
        agent.generateFrom(dispatch -> {
            for (int i = 0; i < 5; i++) {
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

        System.out.println("* Starting worker process");
        File classPath = getClassPath();
        ProcessBuilder processBuilder = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-cp",
                classPath.getAbsolutePath(), WorkerMain.class.getName(), transport.name(), agent.getConfig());
        processBuilder.inheritIO().start().waitFor();

        System.out.println("* Waiting for generator completion");
        agent.waitForCompletion();
    }

    private static File getClassPath() throws URISyntaxException {
        URL codeSource = Main.class.getProtectionDomain().getCodeSource().getLocation();
        if (!codeSource.getProtocol().equals("file")) {
            throw new RuntimeException("Cannot calculate classpath from " + codeSource);
        }
        return new File(codeSource.toURI());
    }

    private static GeneratingAgent createAgent(Transport transport) {
        switch (transport) {
            case Tcp:
                return new TcpGeneratingAgent();
            case File:
                return new FileGeneratingAgent();
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Transport toTransport(String arg) {
        for (Transport transport : Transport.values()) {
            if (transport.name().equalsIgnoreCase(arg)) {
                return transport;
            }
        }
        throw new RuntimeException("Unknown transport " + arg);
    }
}
