package net.rubygrapefruit.ipc.message;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class BufferingOutputStreamBackedSerializer extends BufferBackedSerializer {
    private final OutputStream outputStream;

    public BufferingOutputStreamBackedSerializer(OutputStream outputStream) {
        super(ByteBuffer.allocate(1024));
        this.outputStream = outputStream;
    }

    @Override
    protected void write(ByteBuffer buffer) throws IOException {
        int offset = buffer.arrayOffset() + buffer.position();
        int count = buffer.remaining();
        outputStream.write(buffer.array(), offset, count);
        buffer.position(buffer.limit());
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        outputStream.flush();
    }
}
