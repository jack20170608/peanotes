package top.ilovemyhome.peanotes.security.domain.common;

import java.util.Objects;

public class RankEntity<T> {
    public final T target;
    public final int index;

    public RankEntity(T t, int i) {
        target = t;
        index = i;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RankEntity<?> that = (RankEntity<?>) o;
        return index == that.index && Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, index);
    }
}
