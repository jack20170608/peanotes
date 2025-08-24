package top.ilovemyhome.commons.flywaycli.command;

import org.flywaydb.core.api.callback.Callback;

import java.util.Arrays;
import java.util.Map;

public record FlywayConfig(
    String workDir,
    String env,
    String driverClass,
    String url,
    String user,
    String password,
    String[] locations,
    String schema,
    String table,
    Map<String, String> placeholders,
    Callback callback) {

    @Override
    public String toString() {
        return "FlywayConfig{" +
            "workDir='" + workDir + '\'' +
            ", env='" + env + '\'' +
            ", driverClass='" + driverClass + '\'' +
            ", url='" + url + '\'' +
            ", user='" + user + '\'' +
            ", password='" + password + '\'' +
            ", locations=" + Arrays.toString(locations) +
            ", schema='" + schema + '\'' +
            ", table='" + table + '\'' +
            ", placeholders=" + placeholders +
            ", callback=" + callback +
            '}';
    }
}
