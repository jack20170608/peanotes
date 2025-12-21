package top.ilovemyhome.benchmark.si;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DataSourceConfigTest {

    @Test
    public void testDataSourceConfigBuilder() {
        DataSourceConfig dataSourceConfig = DataSourceConfig.builder()
            .withDriverClassName("com.mysql.jdbc.Driver")
            .withUrl("jdbc:mysql://localhost:3306/test")
            .withUsername("root")
            .withPassword("123456")
            .withAutoCommit(true)
            .withAdditionalProps(Map.of("p1", "v1", "useSSL", false))
            .build();
        assertThat(dataSourceConfig.driverClassName()).isEqualTo("com.mysql.jdbc.Driver");
        assertThat(dataSourceConfig.url()).isEqualTo("jdbc:mysql://localhost:3306/test");
        assertThat(dataSourceConfig.username()).isEqualTo("root");
        assertThat(dataSourceConfig.password()).isEqualTo("123456");
        assertThat(dataSourceConfig.autoCommit()).isEqualTo(true);
        assertThat(dataSourceConfig.additionalProps()).isEqualTo(Map.of("p1", "v1", "useSSL", false));
    }
}
