package top.ilovemyhome.peanotes.keepie.server.config;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.ilovemyhome.peanotes.backend.common.config.ConfigLoader;
import top.ilovemyhome.peanotes.keepie.server.AppContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class KeepieConfig {

    public KeepieConfig(AppContext appContext) {
        String env = appContext.getEnv();
        String rootConfig = "security/keepie.conf";
        String envConfig = "security/keepie-" + env + ".conf";
        Config config = ConfigLoader.loadConfig(rootConfig, envConfig);
        List<? extends Config> keepieConfigList = config.getConfigList("keepie-config-list");
        Map<String, SecretItemConfig> configMap = new HashMap<>(keepieConfigList.size());
        keepieConfigList.forEach(c -> {
            SecretItemConfig secretItem = ConfigBeanFactory.create(c, SecretItemConfig.class);
            configMap.put(secretItem.getName(), secretItem);
        });
        this.secretItemMap = ImmutableMap.copyOf(configMap);
        logger.info("Init [{}] secret items.", secretItemMap.size());
    }

    public SecretItemConfig getSecretItemConfig(String name) {
        if (Objects.isNull(name)){
            return null;
        }
        return secretItemMap.get(name);
    }

    private final Map<String, SecretItemConfig> secretItemMap;

    private static final Logger logger = LoggerFactory.getLogger(KeepieConfig.class);

}
