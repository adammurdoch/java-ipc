package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.Serializer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedFileBackedSerializer implements Serializer, Closeable {
    private static final int SIZE_INCREMENT = 1024 * 32;
    private MappedByteBuffer sendBuffer;
    private final RandomAccessFile backingFile;
    private int writePos;

    public MemoryMappedFileBackedSerializer(File file) throws IOException {
        backingFile = new RandomAccessFile(file, "rw");
        this.sendBuffer = backingFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, SIZE_INCREMENT);
        writePos = 4;
        updateHeader();
        flush();
    }

    @Override
    public void close() throws IOException {
        flush();
        backingFile.close();
    }

    private void ensure(int count) throws IOException {
        int requiredLimit = writePos + count;
        if (requiredLimit <= sendBuffer.capacity()) {
            return;
        }
        long newSize = ((requiredLimit / SIZE_INCREMENT) + 1) * SIZE_INCREMENT;
        sendBuffer = backingFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, newSize);
    }

    @Override
    public void writeString(String string) throws IOException {
        byte[] bytes = string.getBytes();
        ensure(4);
        sendBuffer.putInt(writePos, bytes.length);
        writePos += 4;
        ensure(bytes.length);
        sendBuffer.position(writePos);
        sendBuffer.put(bytes);
        writePos += bytes.length;
        updateHeader();
    }

    private void updateHeader() {
        sendBuffer.putInt(0, writePos);
    }

    @Override
    public void flush() throws IOException {
        sendBuffer.force();
    }
}
