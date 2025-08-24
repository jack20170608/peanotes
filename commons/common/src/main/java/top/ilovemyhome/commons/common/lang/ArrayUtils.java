package top.ilovemyhome.commons.common.lang;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ArrayUtils {

    public static <T> List<T> toList(T[] input) {
        if (Objects.isNull(input)){
            return null;
        }else {
            return Stream.of(input).toList();
        }
    }

    public static <T> Set<T> toSet(T[] input) {
        if (Objects.isNull(input)){
            return null;
        }else {
            return Stream.of(input).collect(Collectors.toSet());
        }
    }

    private ArrayUtils() {
    }
}
