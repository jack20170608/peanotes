package top.ilovemyhome.commons.database;

import com.typesafe.config.Config;
import top.ilovemyhome.commons.common.config.ConfigLoader;

public class SharedResources {

    public static final Config config = ConfigLoader.loadConfigByEnv("test");
}
