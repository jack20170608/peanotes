package top.ilovemyhome.commons.common.util;

import java.util.Map;

public final class MapUtil {

    private MapUtil() {
        // Utility class, prevent instantiation
    }

    public static <K, V> String toString(Map<K, V> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry<K, V> entry : map.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
        }
        // Remove the last comma and space
        sb.setLength(sb.length() - 2);
        sb.append("}");
        return sb.toString();
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }
}
