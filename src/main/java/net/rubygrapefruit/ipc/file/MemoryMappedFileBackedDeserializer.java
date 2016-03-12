package net.rubygrapefruit.ipc.file;

import net.rubygrapefruit.ipc.message.Deserializer;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedFileBackedDeserializer implements Deserializer, Closeable {
    private final MappedByteBuffer receiveBuffer;
    private final RandomAccessFile backingFile;
    private int maxReadPos;
    private int readPos;

    public MemoryMappedFileBackedDeserializer(File file) throws IOException {
        backingFile = new RandomAccessFile(file, "r");
        receiveBuffer = backingFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, 4096);
        maxReadPos = 0;
        readPos = 4;
    }

    @Override
    public void close() throws IOException {
        backingFile.close();
    }

    private void ensure(int length) {
        int requiredLimit = readPos + length;
        if (requiredLimit <= maxReadPos) {
            return;
        }
        while ((maxReadPos = receiveBuffer.getInt(0)) < requiredLimit) {
            ;
        }
    }

    @Override
    public String readString() throws IOException {
        int length = readInteger();
        byte[] bytes = readBytes(length);
        return new String(bytes);
    }

    private byte[] readBytes(int length) {
        ensure(length);
        byte[] result = new byte[length];
        receiveBuffer.position(readPos);
        receiveBuffer.get(result);
        readPos += length;
        return result;
    }

    private int readInteger() {
        ensure(4);
        int result = receiveBuffer.getInt(readPos);
        readPos += 4;
        return result;
    }
}
