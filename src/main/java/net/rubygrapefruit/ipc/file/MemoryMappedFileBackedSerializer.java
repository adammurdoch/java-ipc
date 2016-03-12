package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.Serializer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedFileBackedSerializer implements Serializer, Closeable {
    private static final int SIZE_INCREMENT = 4096;
    private final MappedByteBuffer sendBuffer;
    private final RandomAccessFile backingFile;
    private int writePos;

    public MemoryMappedFileBackedSerializer(File file) throws IOException {
        backingFile = new RandomAccessFile(file, "rw");
        this.sendBuffer = backingFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, SIZE_INCREMENT);
        writePos = 4;
        flush();
    }

    @Override
    public void close() throws IOException {
        backingFile.close();
    }

    @Override
    public void writeString(String string) throws IOException {
        byte[] bytes = string.getBytes();
        sendBuffer.putInt(writePos, bytes.length);
        writePos += 4;
        sendBuffer.position(writePos);
        sendBuffer.put(bytes);
        writePos += bytes.length;
    }

    @Override
    public void flush() throws IOException {
        sendBuffer.force();
        sendBuffer.putInt(0, writePos);
        sendBuffer.force();
    }
}
