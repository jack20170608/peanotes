package top.ilovemyhome.peanotes.common.task.exe.domain;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.peanotes.backend.common.json.JacksonUtil;
import top.ilovemyhome.peanotes.common.task.exe.domain.enums.RegistType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RegistryParamTest {

    @Test
    public void testRegistryParamToJson(){
        RegistryParam registryParam = new RegistryParam(RegistType.EXECUTOR, "foo", "http://localhost:12345");
        System.out.println(JacksonUtil.toJson(registryParam));
        assertThat(JacksonUtil.toJson(registryParam)).isEqualToIgnoringWhitespace("""
            {
              "registryType" : "EXECUTOR",
              "appName" : "foo",
              "address" : "http://localhost:12345"
            }
            """);
    }
}
