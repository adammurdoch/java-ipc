package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.Deserializer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public abstract class MappedByteBufferBackedDeserializer implements Deserializer, Closeable {
    private final RandomAccessFile backingFile;
    private MappedByteBuffer buffer;
    private int maxReadPos;
    private int readPos;

    public MappedByteBufferBackedDeserializer(File file) throws IOException {
        backingFile = new RandomAccessFile(file, "r");
        maxReadPos = 0;
        readPos = 4;
    }

    @Override
    public void close() throws IOException {
        backingFile.close();
    }

    private void ensure(int count) throws IOException {
        int requiredSize = readPos + count;
        if (requiredSize <= maxReadPos) {
            return;
        }
        if (buffer == null) {
            // Wait for enough content in file
            long newSize;
            while ((newSize = backingFile.length()) < requiredSize) {
                Thread.yield();
            }
            buffer = backingFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, newSize);
        }
        while ((maxReadPos = readLimit(buffer)) < requiredSize) {
            Thread.yield();
        }
        if (maxReadPos > buffer.capacity()) {
            // File has grown since buffer mapped
            long newSize = backingFile.length();
            buffer = backingFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, newSize);
        }
    }

    protected abstract int readLimit(MappedByteBuffer buffer);

    @Override
    public String readString() throws IOException {
        int length = readInteger();
        byte[] bytes = readBytes(length);
        return new String(bytes);
    }

    private byte[] readBytes(int length) throws IOException {
        ensure(length);
        byte[] result = new byte[length];
        buffer.position(readPos);
        buffer.get(result);
        readPos += length;
        return result;
    }

    private int readInteger() throws IOException {
        ensure(4);
        int result = buffer.getInt(readPos);
        readPos += 4;
        return result;
    }
}
