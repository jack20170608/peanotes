package top.ilovemyhome.benchmark.si;

import java.util.Map;

public record DataSourceConfig(
    String driverClassName
    , String url
    , String username
    , String password
    , boolean autoCommit
    , Map<String, Object> additionalProps
) {

    public DataSourceConfig {
        if (driverClassName == null || driverClassName.isEmpty()) {
            throw new IllegalArgumentException("driverClassName must not be null or empty");
        }
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("url must not be null or empty");
        }
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("username must not be null or empty");
        }
    }
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String driverClassName;
        private String url;
        private String username;
        private String password;
        private boolean autoCommit;
        private Map<String, Object> additionalProps;

        private Builder() {
        }

        public Builder withDriverClassName(String driverClassName) {
            this.driverClassName = driverClassName;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder withAutoCommit(boolean autoCommit) {
            this.autoCommit = autoCommit;
            return this;
        }

        public Builder withAdditionalProps(Map<String, Object> additionalProps) {
            this.additionalProps = additionalProps;
            return this;
        }

        public DataSourceConfig build() {
            return new DataSourceConfig(driverClassName, url, username, password, autoCommit, additionalProps);
        }
    }
}
