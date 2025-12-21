package top.ilovemyhome.benchmark.si;

import org.junit.jupiter.api.Test;
import top.ilovemyhome.benchmark.si.enums.BenchmarkType;
import top.ilovemyhome.benchmark.si.enums.JdbcClientType;
import top.ilovemyhome.benchmark.si.enums.JdbcConnectionPoolType;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BenchmarkTestCaseTest {

    @Test
    void builder() {
        BenchmarkTestCase testCase = BenchmarkTestCase.builder()
                .withId(1L)
                .withName("test")
                .withType(BenchmarkType.TPC_A)
                .withJdbcClientType(JdbcClientType.JDBC)
                .withConnectionPoolType(JdbcConnectionPoolType.HIKARICP)
                .withDataSourceConfig(new DataSourceConfig("org.postgresql.Driver"
                    , "jdbc:postgresql://localhost:5432/test"
                    , "postgres", "postgres"
                    , true, null))
                .withTestRound(1)
                .withThreadCount(2)
                .withTransactionPerThread(10)
                .withCreateDt(LocalDateTime.now())
                .withLastUpdateDt(LocalDateTime.now())
                .build();
        assertNotNull(testCase);
        assertEquals(1L, testCase.id());
        assertEquals("test", testCase.name());
        assertEquals(BenchmarkType.TPC_A, testCase.type());
        assertEquals(JdbcClientType.JDBC, testCase.jdbcClientType());
        assertEquals(JdbcConnectionPoolType.HIKARICP, testCase.connectionPoolType());
        assertNotNull(testCase.dataSourceConfig());
        assertThat(testCase.dataSourceConfig().driverClassName()).isEqualTo("org.postgresql.Driver");
        assertEquals(1, testCase.testRound());
        assertEquals(2, testCase.threadCount());
        assertEquals(10, testCase.transactionPerThread());
        assertNotNull(testCase.createDt());
        assertNotNull(testCase.lastUpdateDt());
    }
}
