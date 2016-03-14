package net.rubygrapefruit.ipc.worker;

import net.rubygrapefruit.ipc.Transport;
import net.rubygrapefruit.ipc.file.FileReceivingAgent;
import net.rubygrapefruit.ipc.message.Message;
import net.rubygrapefruit.ipc.message.ReceivingAgent;
import net.rubygrapefruit.ipc.tcp.TcpReceivingAgent;

public class WorkerMain {
    public static void main(String[] args) throws Exception {
        Transport transport = Transport.valueOf(args[0]);
        boolean slow = args[1].equals("true");
        System.out.println("* Worker transport: " + transport);
        System.out.println("* Worker slow: " + slow);

        System.out.println("* Worker starting connection");
        ReceivingAgent agent = createAgent(transport);
        agent.receiveTo((message, context) -> {
            if (slow) {
                System.out.println("* Worker received: " + message.text);
            }
            context.send(new Message("start: " + message.text));
            context.send(new Message("status"));
            context.send(new Message("finished"));
            if (message.text.equals("done")) {
                context.send(message);
                context.done();
            }
        });
        agent.setConfig(args[2]);
        agent.start();
        System.out.println("* Worker waiting for receiver to complete");
        agent.waitForCompletion();
        System.out.println("* Worker done");
    }

    private static ReceivingAgent createAgent(Transport transport) {
        switch (transport) {
            case Tcp:
                return new TcpReceivingAgent();
            case File:
                return new FileReceivingAgent();
            default:
                throw new IllegalArgumentException();
        }
    }
}
