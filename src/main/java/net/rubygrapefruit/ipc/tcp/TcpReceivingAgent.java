package net.rubygrapefruit.ipc.tcp;

import net.rubygrapefruit.ipc.message.Deserializer;
import net.rubygrapefruit.ipc.message.InputStreamBackedDeserializer;
import net.rubygrapefruit.ipc.message.OutputStreamBackedSerializer;
import net.rubygrapefruit.ipc.message.Serializer;

import java.io.IOException;
import java.net.Socket;

public class TcpReceivingAgent extends AbstractTcpReceivingAgent {
    @Override
    protected Deserializer createDeserializer(Socket connection) throws IOException {
        return new InputStreamBackedDeserializer(connection.getInputStream());
    }

    @Override
    protected Serializer createSerializer(Socket connection) throws IOException {
        return new OutputStreamBackedSerializer(connection.getOutputStream());
    }
}
