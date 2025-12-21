package top.ilovemyhome.commons.common.util;

import java.util.Collection;

public class ArrayUtils {

    private ArrayUtils() {
    }

    public static <T> T[] asArray(T... elements) {
        return elements;
    }

    public static <T> T[] asArray(Class<T> clazz, Collection<T> elements) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }
        if (elements == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T[] array = (T[]) java.lang.reflect.Array.newInstance(clazz, elements.size());
        return elements.toArray(array);
    }
}
