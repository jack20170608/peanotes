drop table if exists KARATE_EXECUTION_RESULT cascade;
drop sequence if exists SEQ_KARATE_EXECUTION_RESULT_ID cascade;

create sequence SEQ_KARATE_EXECUTION_RESULT_ID increment by 1 minvalue 1
  no maxvalue start with 1;

create table KARATE_EXECUTION_RESULT
(
  id                  NUMERIC(18, 0) primary key default nextval('SEQ_KARATE_EXECUTION_RESULT_ID'),
  karate_execution_id NUMERIC(18, 0) not null,
  thread_count        integer,
  feature_count       integer,
  pass_count          integer,
  fail_count          integer,
  skip_count          integer,
  start_dt            timestamp,
  end_dt              timestamp,
  take_time           NUMERIC(18, 0),
  failed_map          text,
  failed_reason       varchar(1024)
);

create index idx_KARATE_EXECUTION_RESULT_execution_id on KARATE_EXECUTION_RESULT (karate_execution_id);

