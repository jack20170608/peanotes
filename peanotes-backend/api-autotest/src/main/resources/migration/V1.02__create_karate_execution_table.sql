drop table if exists KARATE_EXECUTION cascade;
drop sequence if exists SEQ_KARATE_EXECUTION_ID cascade;

create sequence SEQ_KARATE_EXECUTION_ID increment by 1 minvalue 1
  no maxvalue start with 1;

create table KARATE_EXECUTION(
  id NUMERIC(18,0) primary key default nextval('SEQ_KARATE_EXECUTION_ID'),
  karate_request_id NUMERIC(18,0) not null ,
  status VARCHAR(32) not null ,
  report_path VARCHAR(512) ,
  create_dt timestamp,
  start_dt timestamp,
  end_dt timestamp
);

create index idx_KARATE_EXECUTION_request_id on KARATE_EXECUTION(karate_request_id);

