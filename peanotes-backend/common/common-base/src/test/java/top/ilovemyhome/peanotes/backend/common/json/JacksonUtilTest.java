package top.ilovemyhome.peanotes.backend.common.json;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.domain.People;
import top.ilovemyhome.peanotes.backend.common.resource.ResourceUtil;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class JacksonUtilTest {

    @BeforeAll
    public static void initData() {
        String jsonStr = ResourceUtil.getClasspathResourceAsString("data/all-people.json");
        DATAS.addAll(Objects.requireNonNull(JacksonUtil.fromJson(jsonStr, new TypeReference<>() {})));
        assertThat(DATAS.size()).isEqualTo(2);
    }

    @Test
    public void testJsonSerialization() {
        Set<People> peopleSet = JacksonUtil.fromJson(JacksonUtil.toJson(DATAS), new TypeReference<>() {});
        assertThat(peopleSet).isEqualTo(DATAS);
    }

    private static final Set<People> DATAS = new HashSet<>();

}



