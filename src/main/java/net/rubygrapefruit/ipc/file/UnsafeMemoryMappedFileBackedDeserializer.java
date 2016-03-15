package net.rubygrapefruit.ipc.file;

import sun.misc.Unsafe;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.MappedByteBuffer;

public class UnsafeMemoryMappedFileBackedDeserializer extends MappedByteBufferBackedDeserializer {
    private final Unsafe unsafe;
    private final Field addressField;

    public UnsafeMemoryMappedFileBackedDeserializer(File file) throws IOException {
        super(file);
        try {
            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
            addressField = Buffer.class.getDeclaredField("address");
            addressField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected int readLimit(MappedByteBuffer buffer) {
        long address;
        try {
            address = addressField.getLong(buffer);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return unsafe.getIntVolatile(null, address);
    }
}
