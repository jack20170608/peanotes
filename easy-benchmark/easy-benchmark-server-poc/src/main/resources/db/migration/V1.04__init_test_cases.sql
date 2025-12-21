INSERT into benchmark_test_case
( ID
, NAME
, TYPE
, JDBC_CLIENT_TYPE
, CONNECTION_POOL_TYPE
, DATA_SOURCE_CONFIG
, TEST_ROUND
, THREAD_COUNT
, TRANSACTION_PER_THREAD
)
values (1, 'TPCA-HIKARI-1-1-100', 'TPC_A', 'JDBC', 'HIKARICP',
'{"driverClassName": "org.postgresql.Driver", "url": "jdbc:postgresql://10.10.10.20:5432/benchmark", "username": "jack", "password": "1", "autoCommit": false}',
1, 1, 100)
, (2, 'TPCA-HIKARI-1-10-100', 'TPC_A', 'JDBC', 'HIKARICP',
'{"driverClassName": "org.postgresql.Driver", "url": "jdbc:postgresql://10.10.10.20:5432/benchmark", "username": "jack", "password": "1", "autoCommit": false}',
1, 10, 100);
;
