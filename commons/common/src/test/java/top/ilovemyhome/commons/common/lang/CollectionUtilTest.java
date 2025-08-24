package top.ilovemyhome.commons.common.lang;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.commons.common.util.CollectionUtil;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CollectionUtilTest {

    @Test
    public void testIsEmpty(){
        assertThat(CollectionUtil.isEmpty(null)).isTrue();
        assertThat(CollectionUtil.isEmpty(List.of())).isTrue();
        assertThat(CollectionUtil.isEmpty(List.of(1))).isFalse();
        assertThat(CollectionUtil.isEmpty(Set.of())).isTrue();
        assertThat(CollectionUtil.isEmpty(Set.of(2))).isFalse();
    }
}
