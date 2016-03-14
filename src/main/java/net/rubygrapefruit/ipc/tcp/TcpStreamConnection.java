package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.InputStreamBackedDeserializer;
import net.rubygrapefruit.ipc.message.OutputStreamBackedSerializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TcpStreamConnection implements Connection {
    private final Socket socket;
    private final InputStreamBackedDeserializer receive;
    private final OutputStreamBackedSerializer send;

    public TcpStreamConnection(int port) throws IOException {
        this(new Socket(InetAddress.getLoopbackAddress(), port));
    }

    public TcpStreamConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.receive = new InputStreamBackedDeserializer(socket.getInputStream());
        this.send = new OutputStreamBackedSerializer(socket.getOutputStream());
    }

    @Override
    public Deserializer getReceive() {
        return receive;
    }

    @Override
    public Serializer getSend() {
        return send;
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
