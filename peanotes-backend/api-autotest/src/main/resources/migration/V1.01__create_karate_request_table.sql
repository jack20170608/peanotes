drop table if exists KARATE_REQUEST cascade;
drop sequence if exists SEQ_KARATE_REQUEST_ID cascade;

create sequence SEQ_KARATE_REQUEST_ID increment by 1 minvalue 1
  no maxvalue start with 1;

create table KARATE_REQUEST(
  id NUMERIC(18,0) primary key default nextval('SEQ_KARATE_REQUEST_ID'),
  name VARCHAR(64) not null ,
  sequence_no VARCHAR(32) not null ,
  create_dt timestamp,
  service_name VARCHAR(64),
  env VARCHAR(16),
  feature_file_names TEXT,
  tags VARCHAR(1024)
);

create index idx_KARATE_REQUEST_service_name on  KARATE_REQUEST(service_name);

