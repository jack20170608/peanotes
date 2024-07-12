package top.ilovemyhome.peanotes.backend.common.db;

import com.typesafe.config.Config;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public final class FlyWayHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlyWayHelper.class);


    public static void run(Config config){
        boolean enabled = config.getBoolean("flyway.enabled");
        String dirver = config.getString("database.driver");
        String url = config.getString("database.url");
        String user = config.getString("database.user");
        String password = config.getString("database.password");

        String metaTable = config.getString("flyway.table");
        List<String> locations = config.getStringList("flyway.locations");

        LOGGER.info("=====================================================");
        LOGGER.info("url=[{}].", url);
        LOGGER.info("driver=[{}].", dirver);
        LOGGER.info("user=[{}], password=[{}].", user, password);
        LOGGER.info("schemaHisTable=[{}].", metaTable);
        LOGGER.info("locations=[{}]", String.join(",", locations));
        LOGGER.info("=====================================================");

        if (enabled) {
            Flyway flyway = Flyway.configure()
                .encoding("utf-8")
                .driver(dirver)
                .dataSource(url, user, password)
                .table(metaTable)
                .placeholders(Map.of("foo", "bar"))
                .locations(locations.toArray(new String[locations.size()]))
                .schemas("public")
                .load();

            flyway.baseline();
            flyway.repair();

            flyway.migrate();
        }else {
            LOGGER.info("Flyway not enabled.");
        }

    }

}
