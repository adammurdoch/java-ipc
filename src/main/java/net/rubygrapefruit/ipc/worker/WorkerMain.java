package net.rubygrapefruit.ipc.worker;

import net.rubygrapefruit.ipc.Message;
import net.rubygrapefruit.ipc.tcp.TcpClient;

public class WorkerMain {
    public static void main(String[] args) throws Exception {
        System.out.println("* Worker starting connection");
        TcpClient client = new TcpClient();
        client.receiveTo((message, context) -> {
            System.out.println("* Worker received: " + message.text);
            context.send(new Message("ok"));
            if (message.text.equals("done")) {
                context.send(message);
                context.done();
            }
        });
        client.setConfig(args[0]);
        client.start();
        System.out.println("* Worker stopping connection");
        client.stop();
    }
}
