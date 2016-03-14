package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TcpStreamConnection implements Connection {
    private final Socket socket;
    private final Deserializer receive;
    private final Serializer send;

    public TcpStreamConnection(int port, boolean buffered) throws IOException {
        this(new Socket(InetAddress.getLoopbackAddress(), port), buffered);
    }

    public TcpStreamConnection(Socket socket, boolean buffered) throws IOException {
        this.socket = socket;
        this.receive = buffered ? new BufferingInputStreamBackedDeserializer(socket.getInputStream())
                : new InputStreamBackedDeserializer(socket.getInputStream());
        this.send = buffered ? new BufferingOutputStreamBackedSerializer(socket.getOutputStream())
                : new OutputStreamBackedSerializer(socket.getOutputStream());
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
