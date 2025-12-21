drop table if exists benchmark_test_result;

--Create the test case table
create table benchmark_test_result
(
  ID                         NUMERIC(22)  not null primary key,
  TEST_CASE_ID               NUMERIC(22)  not null,
  START_DT                   timestamp default current_timestamp,
  END_DT                     timestamp default current_timestamp,
  TOTAL_TIME_MS              numeric(22),
  STATE                      varchar(255) not null,
  SUCCESS                    BOOLEAN      not null,
  ERROR_MSG                  TEXT,
  ERROR_STACK_TRACE          TEXT,
  TEST_ROUND                 INTEGER,
  TOTAL_THREAD_COUNT         INTEGER,
  FAILED_THREAD_COUNT        INTEGER,
  SUCCESS_THREAD_COUNT       INTEGER,
  TOTAL_TRANSACTION_COUNT    INTEGER,
  COMMIT_TRANSACTION_COUNT   INTEGER,
  ROLLBACK_TRANSACTION_COUNT INTEGER,
  AVG_COMMIT_TIME_MS         numeric(22, 2),
  AVG_ROLLBACK_TIME_MS       numeric(22, 2),
  TPS                        numeric(22, 2),
  SUCCESS_RATE               numeric(10, 2),
  CREATE_DT                  timestamp default current_timestamp,
  LAST_UPDATE_DT             timestamp default current_timestamp
);

