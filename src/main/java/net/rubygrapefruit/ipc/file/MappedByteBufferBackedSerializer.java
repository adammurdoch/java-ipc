package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.Serializer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public abstract class MappedByteBufferBackedSerializer implements Serializer, Closeable {
    private static final int SIZE_INCREMENT = 1024 * 32;
    private final RandomAccessFile backingFile;
    private MappedByteBuffer buffer;
    private int writePos;

    public MappedByteBufferBackedSerializer(File file) throws IOException {
        backingFile = new RandomAccessFile(file, "rw");
        this.buffer = backingFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, SIZE_INCREMENT);
        writePos = 4;
        buffer.putInt(0, 0);
        buffer.force();
    }

    @Override
    public void close() throws IOException {
        flush();
        backingFile.close();
    }

    private void ensure(int count) throws IOException {
        int requiredLimit = writePos + count;
        if (requiredLimit <= buffer.capacity()) {
            return;
        }
        long newSize = ((requiredLimit / SIZE_INCREMENT) + 1) * SIZE_INCREMENT;
        buffer = backingFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, newSize);
    }

    @Override
    public void writeString(String string) throws IOException {
        byte[] bytes = string.getBytes();
        ensure(4);
        buffer.putInt(writePos, bytes.length);
        writePos += 4;
        ensure(bytes.length);
        buffer.position(writePos);
        buffer.put(bytes);
        writePos += bytes.length;
        updateHeader(buffer, writePos);
    }

    protected abstract void updateHeader(MappedByteBuffer sendBuffer, int writePos);

    @Override
    public void flush() throws IOException {
        buffer.force();
    }
}
