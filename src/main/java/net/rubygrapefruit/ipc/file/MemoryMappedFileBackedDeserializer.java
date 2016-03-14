package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.Deserializer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedFileBackedDeserializer implements Deserializer, Closeable {
    private final RandomAccessFile backingFile;
    private final File file;
    private MappedByteBuffer receiveBuffer;
    private int maxReadPos;
    private int readPos;

    public MemoryMappedFileBackedDeserializer(File file) throws IOException {
        this.file = file;
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
        if (receiveBuffer == null) {
            // Wait for enough content in file
            long newSize;
            while ((newSize = backingFile.length()) < requiredSize) {
                Thread.yield();
            }
            receiveBuffer = backingFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, newSize);
        }
        while ((maxReadPos = receiveBuffer.getInt(0)) < requiredSize) {
            Thread.yield();
        }
        if (maxReadPos > receiveBuffer.capacity()) {
            // File has grown since buffer mapped
            long newSize = backingFile.length();
            receiveBuffer = backingFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, newSize);
        }
    }

    @Override
    public String readString() throws IOException {
        int length = readInteger();
        byte[] bytes = readBytes(length);
        return new String(bytes);
    }

    private byte[] readBytes(int length) throws IOException {
        ensure(length);
        byte[] result = new byte[length];
        receiveBuffer.position(readPos);
        receiveBuffer.get(result);
        readPos += length;
        return result;
    }

    private int readInteger() throws IOException {
        ensure(4);
        int result = receiveBuffer.getInt(readPos);
        readPos += 4;
        return result;
    }
}
