package net.rubygrapefruit.ipc;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.rubygrapefruit.ipc.agent.FlushStrategy;
import net.rubygrapefruit.ipc.agent.GeneratingAgent;
import net.rubygrapefruit.ipc.agent.Throughput;
import net.rubygrapefruit.ipc.file.FileGeneratingAgent;
import net.rubygrapefruit.ipc.message.*;
import net.rubygrapefruit.ipc.tcp.TcpChannelGeneratingAgent;
import net.rubygrapefruit.ipc.tcp.TcpGeneratingAgent;
import net.rubygrapefruit.ipc.worker.WorkerMain;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws Exception {
        OptionParser optionParser = new OptionParser();
        optionParser.accepts("transport").withRequiredArg().required();
        optionParser.accepts("slow");
        optionParser.accepts("flush");
        OptionSet optionSet = optionParser.parse(args);
        Transport transport = toTransport(optionSet.valueOf("transport").toString());
        final Throughput throughput = optionSet.has("slow") ? Throughput.Slow : Throughput.Fast;
        FlushStrategy flush = optionSet.has("flush") ? FlushStrategy.EachMessage : FlushStrategy.EndStream;

        System.out.println("* Transport: " + transport);
        System.out.println("* Throughput: " + throughput);
        System.out.println("* Flush: " + flush);

        System.out.println("* Starting generator");
        GeneratingAgent agent = createAgent(transport);
        agent.generateFrom(new Generator() {
            @Override
            public void generate(Dispatch dispatch) throws IOException {
                int messageCount = throughput == Throughput.Fast ? 100000 : 10;
                for (int i = 0; i < messageCount; i++) {
                    dispatch.send(new Message(String.valueOf(i)));
                    if (throughput == Throughput.Slow) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                dispatch.send(new Message("done"));
            }});
        agent.receiveTo(new Receiver() {
            @Override
            public void receive(Message message, ReceiveContext context) throws IOException {
                if (message.text.equals("done")) {
                    context.done();
                }
            }});

        long start = System.currentTimeMillis();

        agent.start(flush);

        System.out.println("* Starting worker process");
        File classPath = getClassPath();
        ProcessBuilder processBuilder = new ProcessBuilder(System.getProperty("java.home") + "/bin/java", "-cp",
                classPath.getAbsolutePath(), WorkerMain.class.getName(), transport.name(), throughput.name(), flush.name(), agent.getConfig());
        processBuilder.inheritIO().start().waitFor();

        System.out.println("* Waiting for generator completion");
        agent.waitForCompletion();

        long end = System.currentTimeMillis();

        System.out.println("* TOTAL TIME: " + (end - start));
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
                return new TcpGeneratingAgent(false);
            case TcpBuffered:
                return new TcpGeneratingAgent(true);
            case TcpChannel:
                return new TcpChannelGeneratingAgent();
            case File:
                return new FileGeneratingAgent(false);
            case FileUnsafe:
                return new FileGeneratingAgent(true);
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
