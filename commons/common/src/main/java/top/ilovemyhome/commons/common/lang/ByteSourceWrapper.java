package top.ilovemyhome.commons.common.lang;

import top.ilovemyhome.commons.common.util.ByteUtils;

import java.io.Closeable;
import java.io.IOException;

/**
 * To use try-with-resources idiom, this class supports wrapping existing ByteSource
 * object or byte array. At end of try block, it gets zeroed out automatically.
 */
public final class ByteSourceWrapper implements Closeable {
    private final byte[] bytes;

    private ByteSourceWrapper(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * This method generically accepts byte array or ByteSource instance.
     */
    public static ByteSourceWrapper wrap(Object value) {
        if (value instanceof byte[]) {
            byte[] bytes = (byte[]) value;
            return new ByteSourceWrapper(bytes);
        } else if (value instanceof ByteSource) {
            byte[] bytes = ((ByteSource) value).getBytes();
            return new ByteSourceWrapper(bytes);
        }
        throw new IllegalArgumentException();
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void close() throws IOException {
        ByteUtils.wipe(bytes);
    }
}
