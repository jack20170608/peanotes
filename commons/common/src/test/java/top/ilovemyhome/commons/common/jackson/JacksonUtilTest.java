package top.ilovemyhome.commons.common.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.domain.People;
import top.ilovemyhome.commons.common.io.ResourceUtil;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;


public class JacksonUtilTest {

    @BeforeAll
    public static void initData() {
        String jsonStr = ResourceUtil.getClasspathResourceAsString("data/all-people.json");
        DATAS.addAll(Objects.requireNonNull(JacksonUtil.fromJson(jsonStr, new TypeReference<>() {})));
        assertThat(DATAS.size()).isEqualTo(2);
        logger.info("test logger!");
    }

    @Test
    public void testJsonSerialization() {
        Set<People> peopleSet = JacksonUtil.fromJson(JacksonUtil.toJson(DATAS), new TypeReference<>() {});
        assertThat(peopleSet).isEqualTo(DATAS);
    }

    private static final Set<People> DATAS = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(JacksonUtilTest.class);

}



