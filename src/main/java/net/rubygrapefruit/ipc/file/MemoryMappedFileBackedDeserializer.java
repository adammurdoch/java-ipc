package net.rubygrapefruit.ipc.file;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;

public class MemoryMappedFileBackedDeserializer extends MappedByteBufferBackedDeserializer {
    public MemoryMappedFileBackedDeserializer(File file) throws IOException {
        super(file);
    }

    @Override
    protected int readLimit(MappedByteBuffer buffer) {
        return buffer.getInt(0);
    }
}
