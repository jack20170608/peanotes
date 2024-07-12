package top.ilovemyhome.peanotes.backend.common.config;

import com.typesafe.config.Config;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ConfigLoaderTest {

    @Test
    public void testLoadConf(){
        Config config = ConfigLoader.loadConfig("config/app.conf", "config/app-dev.conf");
        assertThat(config.getString("database.url")).isEqualTo("jdbc:localhost:1234:foo");
        assertThat(config.getString("database.user")).isEqualTo("app_user");
        assertThat(config.getString("database.password")).isEqualTo("1");
        assertThat(config.getStringList("names")).isEqualTo(List.of("jack", "leo", "bill"));
        assertThat(config.getString("app.context-path")).isEqualTo("are you ok");
    }
}
