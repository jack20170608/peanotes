package top.ilovemyhome.peanotes.common.task.admin.domain;

import com.google.common.collect.ImmutableMap;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by xuxueli on 16/9/30.
 */
public class JobRegistry {

    public enum Field {
        id("ID", true)
        , registryGroup("REGISTRY_GROUP")
        , registryKey("REGISTRY_KEY")
        , registryValue("REGISTRY_VALUE")
        , lastUpdateDt("LAST_UPDATE_DT")
        ;

        private final String dbColumn;
        private final boolean isId;

        Field(String dbColumn) {
            this.dbColumn = dbColumn;
            this.isId = false;
        }

        Field(String dbColumn, boolean isId) {
            this.dbColumn = dbColumn;
            this.isId = isId;
        }

        public String getDbColumn() {
            return dbColumn;
        }

        public boolean isId() {
            return isId;
        }
    }

    public static final Map<String, String> FIELD_COLUMN_MAP = ImmutableMap.copyOf(Stream.of(JobRegistry.Field.values())
        .collect(Collectors.toMap(JobRegistry.Field::name, JobRegistry.Field::getDbColumn)));

    public Long getId() {
        return id;
    }

    public String getRegistryGroup() {
        return registryGroup;
    }

    public String getRegistryKey() {
        return registryKey;
    }

    public String getRegistryValue() {
        return registryValue;
    }

    public LocalDateTime getLastUpdateDt() {
        return lastUpdateDt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private LocalDateTime lastUpdateDt;
        private String registryValue;
        private String registryKey;
        private String registryGroup;
        private Long id;

        private Builder() {
        }

        public Builder withLastUpdateDt(LocalDateTime lastUpdateDt) {
            this.lastUpdateDt = lastUpdateDt;
            return this;
        }

        public Builder withRegistryValue(String registryValue) {
            this.registryValue = registryValue;
            return this;
        }

        public Builder withRegistryKey(String registryKey) {
            this.registryKey = registryKey;
            return this;
        }

        public Builder withRegistryGroup(String registryGroup) {
            this.registryGroup = registryGroup;
            return this;
        }

        public Builder withId(Long id) {
            this.id = id;
            return this;
        }

        public JobRegistry build() {
            JobRegistry jobRegistry = new JobRegistry();
            jobRegistry.id = this.id;
            jobRegistry.lastUpdateDt = this.lastUpdateDt;
            jobRegistry.registryGroup = this.registryGroup;
            jobRegistry.registryKey = this.registryKey;
            jobRegistry.registryValue = this.registryValue;
            return jobRegistry;
        }
    }

    private Long id;
    private String registryGroup;
    private String registryKey;
    private String registryValue;
    private LocalDateTime lastUpdateDt;


}
