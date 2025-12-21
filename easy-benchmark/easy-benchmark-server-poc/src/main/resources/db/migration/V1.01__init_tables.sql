drop table if exists benchmark_test_case;

--Create the test case table
create table benchmark_test_case(
  ID                     NUMERIC(22)  not null primary key,
  NAME                   varchar(255) not null,
  TYPE                   varchar(255) not null,
  JDBC_CLIENT_TYPE       varchar(255) not null,
  CONNECTION_POOL_TYPE   varchar(255) not null,
  DATA_SOURCE_CONFIG     text,
  TEST_ROUND             int,
  THREAD_COUNT           int,
  TRANSACTION_PER_THREAD int,
  CREATE_DT              timestamp default current_timestamp,
  LAST_UPDATE_DT         timestamp default current_timestamp
);

