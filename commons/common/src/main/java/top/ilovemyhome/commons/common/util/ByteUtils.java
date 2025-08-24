package top.ilovemyhome.commons.common.util;

import java.util.Arrays;

public final class ByteUtils {

    private ByteUtils() {
        // private utility class
    }

    /**
     * For security, sensitive information in array should be zeroed-out at end of use (SHIRO-349).
     *
     * @param value An array holding sensitive data
     */
    public static void wipe(Object value) {
        if (value instanceof byte[]) {
            byte[] array = (byte[]) value;
            Arrays.fill(array, (byte) 0);
        } else if (value instanceof char[]) {
            char[] array = (char[]) value;
            Arrays.fill(array, '\u0000');
        }
    }

}
