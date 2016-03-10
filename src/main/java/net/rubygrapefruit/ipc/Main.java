package net.rubygrapefruit.ipc;

import net.rubygrapefruit.ipc.message.Message;
import net.rubygrapefruit.ipc.tcp.TcpServer;
import net.rubygrapefruit.ipc.worker.WorkerMain;

import java.io.File;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("* Starting server");
        TcpServer tcpServer = new TcpServer();
        tcpServer.generateFrom(dispatch -> {
            dispatch.send(new Message("one"));
            dispatch.send(new Message("two"));
            dispatch.send(new Message("three"));
            dispatch.send(new Message("four"));
            dispatch.send(new Message("done"));
        }); tcpServer.receiveTo((message, context) -> {
            System.out.println("* Received " + message.text);
            if (message.text.equals("done")) {
                context.done();
            }
        });
        tcpServer.start();

        System.out.println("* Starting worker");
        URL codeSource = Main.class.getProtectionDomain().getCodeSource().getLocation();
        if (!codeSource.getProtocol().equals("file")) {
            throw new RuntimeException("Cannot calculate classpath from " + codeSource);
        }
        File classPath = new File(codeSource.toURI());
        ProcessBuilder processBuilder = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-cp",
                classPath.getAbsolutePath(), WorkerMain.class.getName(), tcpServer.getConfig());
        processBuilder.inheritIO().start().waitFor();

        System.out.println("* Waiting for completion");
        tcpServer.stop();
    }
}
