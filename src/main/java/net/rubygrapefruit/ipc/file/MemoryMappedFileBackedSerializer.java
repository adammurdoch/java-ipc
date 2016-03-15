package net.rubygrapefruit.ipc.file;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;

public class MemoryMappedFileBackedSerializer extends MappedByteBufferBackedSerializer {
    public MemoryMappedFileBackedSerializer(File file) throws IOException {
        super(file);
    }

    protected void updateHeader(MappedByteBuffer sendBuffer, int writePos) {
        sendBuffer.putInt(0, writePos);
    }
}
