package net.rubygrapefruit.ipc.worker;

import net.rubygrapefruit.ipc.message.Client;
import net.rubygrapefruit.ipc.message.Message;
import net.rubygrapefruit.ipc.tcp.TcpClient;

public class WorkerMain {
    public static void main(String[] args) throws Exception {
        System.out.println("* Worker starting connection");
        Client client = new TcpClient();
        client.receiveTo((message, context) -> {
            context.send(new Message("start: " + message.text));
            context.send(new Message("status"));
            context.send(new Message("finished"));
            if (message.text.equals("done")) {
                context.send(message);
                context.done();
            }
        });
        client.setConfig(args[0]);
        client.start();
        System.out.println("* Worker waiting for completion");
        client.stop();
        System.out.println("* Worker done");
    }
}
