package top.ilovemyhome.peanotes.backend.common.db.utils;

import com.typesafe.config.Config;
import top.ilovemyhome.peanotes.backend.common.config.ConfigLoader;
import top.ilovemyhome.peanotes.backend.common.db.SimpleDataSourceFactory;

public class TestUtils {

    public static SimpleDataSourceFactory getDataSourceFactory() {
        Config config = ConfigLoader.loadConfig("config/common-db.conf"
            , "config/common-db-test.conf");
        return SimpleDataSourceFactory.getInstance(config);
    }

}
