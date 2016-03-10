package net.rubygrapefruit.ipc.worker;

import net.rubygrapefruit.ipc.message.ReceivingAgent;
import net.rubygrapefruit.ipc.message.Message;
import net.rubygrapefruit.ipc.tcp.TcpReceivingAgent;

public class WorkerMain {
    public static void main(String[] args) throws Exception {
        System.out.println("* Worker starting connection");
        ReceivingAgent agent = new TcpReceivingAgent();
        agent.receiveTo((message, context) -> {
            context.send(new Message("start: " + message.text));
            context.send(new Message("status"));
            context.send(new Message("finished"));
            if (message.text.equals("done")) {
                context.send(message);
                context.done();
            }
        });
        agent.setConfig(args[0]);
        agent.start();
        System.out.println("* Worker waiting for completion");
        agent.stop();
        System.out.println("* Worker done");
    }
}
