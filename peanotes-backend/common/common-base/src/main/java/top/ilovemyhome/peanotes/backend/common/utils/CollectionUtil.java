package top.ilovemyhome.peanotes.backend.common.utils;

import java.util.Collection;
import java.util.Optional;

public final class CollectionUtil {
    private CollectionUtil() {}

    public static boolean isEmpty(Collection<?> collection){
        return Optional.ofNullable(collection)
            .map(c -> c.isEmpty())
            .orElse(true);
    }
}
