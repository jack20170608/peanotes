package top.ilovemyhome.commons.database.flyway;

import com.typesafe.config.Config;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.commons.common.config.ConfigLoader;
import top.ilovemyhome.commons.common.util.ArrayUtils;

import java.nio.charset.StandardCharsets;

import static top.ilovemyhome.commons.common.util.MapUtil.toStringMap;

public final class FlyWayHelper {
    private static final Logger logger = LoggerFactory.getLogger(FlyWayHelper.class);

    public static void run(Config rootConfig){
        FlywayConfig config = ConfigLoader.loadConfigAsBean(rootConfig
            , "flyway", FlywayConfig.class);

        boolean enabled = config.isEnabled();

        logger.info("=====================================================");
        logger.info("FlywayConfig=[{}].", config);
        logger.info("=====================================================");

        if (enabled) {
            Flyway flyway = Flyway.configure()
                .encoding(StandardCharsets.UTF_8)
                .driver(config.getDriver())
                .dataSource(config.getUrl(), config.getUser(), config.getPassword())
                .table(config.getMetaTable())
                .placeholders(toStringMap(config.getPlaceHolders()))
                .locations(ArrayUtils.asArray(String.class, config.getLocations()))
                .schemas("public")
                .callbacks(ArrayUtils.asArray(String.class, config.getCallbacks()))
                .load();
            flyway.baseline();
            flyway.repair();
            flyway.migrate();
        }else {
            logger.info("Flyway not enabled.");
        }

    }

}
