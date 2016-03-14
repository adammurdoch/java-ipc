package net.rubygrapefruit.ipc.worker;

import net.rubygrapefruit.ipc.Transport;
import net.rubygrapefruit.ipc.agent.FlushStrategy;
import net.rubygrapefruit.ipc.agent.ReceivingAgent;
import net.rubygrapefruit.ipc.agent.Throughput;
import net.rubygrapefruit.ipc.file.FileReceivingAgent;
import net.rubygrapefruit.ipc.message.Message;
import net.rubygrapefruit.ipc.tcp.TcpChannelReceivingAgent;
import net.rubygrapefruit.ipc.tcp.TcpReceivingAgent;

public class WorkerMain {
    public static void main(String[] args) throws Exception {
        Transport transport = Transport.valueOf(args[0]);
        Throughput throughput = Throughput.valueOf(args[1]);
        FlushStrategy flush = FlushStrategy.valueOf(args[2]);
        System.out.println("* Worker transport: " + transport);
        System.out.println("* Worker throughput: " + throughput);
        System.out.println("* Worker flush: " + flush);

        System.out.println("* Worker starting connection");
        ReceivingAgent agent = createAgent(transport);
        agent.receiveTo((message, context) -> {
            if (throughput == Throughput.Slow) {
                System.out.println("* Worker received: " + message.text);
            }
            context.send(new Message("start: " + message.text));
            for (int i = 0; i < 10; i++) {
                context.send(new Message("status " + i));
            }
            context.send(new Message("finished"));
            if (message.text.equals("done")) {
                context.send(message);
                context.done();
            }
        });
        agent.setConfig(args[3], flush);
        agent.start();
        System.out.println("* Worker waiting for receiver to complete");
        agent.waitForCompletion();
        System.out.println("* Worker done");
    }

    private static ReceivingAgent createAgent(Transport transport) {
        switch (transport) {
            case Tcp:
                return new TcpReceivingAgent();
            case TcpChannel:
                return new TcpChannelReceivingAgent();
            case File:
                return new FileReceivingAgent();
            default:
                throw new IllegalArgumentException();
        }
    }
}
