--1. Create the t_job_info table
drop table if exists t_job_info cascade;
drop sequence if exists seq_t_job_info_id cascade;

create sequence seq_t_job_info_id increment by 1 minvalue 1
  no maxvalue start with 1;


create table t_job_info
(
  id                        bigint primary key    default nextval('seq_t_job_info_id'),
  job_group_id              integer      NOT NULL,
  job_desc                  varchar(255) NOT NULL,
  add_time                  timestamp             DEFAULT NULL,
  update_time               timestamp             DEFAULT NULL,
  author                    varchar(64)           DEFAULT NULL,
  alarm_email               varchar(255)          DEFAULT NULL,
  schedule_type             varchar(50)  NOT NULL DEFAULT 'NONE',
  schedule_conf             varchar(128)          DEFAULT NULL,
  misfire_strategy          varchar(50)  NOT NULL DEFAULT 'DO_NOTHING',
  executor_route_strategy   varchar(50)           DEFAULT NULL,
  executor_handler          varchar(255)          DEFAULT NULL,
  executor_param            varchar(512)          DEFAULT NULL,
  executor_block_strategy   varchar(50)           DEFAULT NULL,
  executor_timeout          integer      NOT NULL DEFAULT '0',
  executor_fail_retry_count integer      NOT NULL DEFAULT '0',
  glue_type                 varchar(50)  NOT NULL,
  glue_source               text,
  glue_remark               varchar(128)          DEFAULT NULL,
  glue_update_time          timestamp             DEFAULT NULL,
  child_job_id              varchar(255)          DEFAULT NULL,
  trigger_status            integer      NOT NULL DEFAULT '0',
  trigger_last_time         bigint       NOT NULL DEFAULT '0',
  trigger_next_time         bigint       NOT NULL DEFAULT '0'
);

--2. Create the t_job_log
drop table if exists t_job_log cascade;
drop sequence if exists seq_t_job_log_id cascade;

create sequence seq_t_job_log_id increment by 1 minvalue 1
  no maxvalue start with 1;

CREATE TABLE t_job_log
(
  id                        bigint primary key default nextval('seq_t_job_log_id'),
  job_group_id              bigint  NOT NULL,
  job_id                    bigint  NOT NULL,
  executor_address          varchar(255)       DEFAULT NULL,
  executor_handler          varchar(255)       DEFAULT NULL,
  executor_param            varchar(512)       DEFAULT NULL,
  executor_sharding_param   varchar(20)        DEFAULT NULL,
  executor_fail_retry_count integer NOT NULL   DEFAULT '0',
  trigger_time              timestamp          DEFAULT NULL,
  trigger_code              integer NOT NULL,
  trigger_msg               text,
  handle_time               timestamp          DEFAULT NULL,
  handle_code               integer NOT NULL,
  handle_msg                text,
  alarm_status              integer NOT NULL   DEFAULT '0'
);

--3. Create the t_job_log_report table
drop table if exists t_job_log_report cascade;
drop sequence if exists seq_t_job_log_report_id cascade;

create sequence seq_t_job_log_report_id increment by 1 minvalue 1
  no maxvalue start with 1;

CREATE TABLE t_job_log_report
(
  id            integer primary key default nextval('seq_t_job_log_report_id'),
  trigger_date  date    NOT NULL,
  running_count integer NOT NULL    DEFAULT '0',
  suc_count     integer NOT NULL    DEFAULT '0',
  fail_count    integer NOT NULL    DEFAULT '0',
  last_update_dt timestamp           DEFAULT NULL
);

--4. Create the t_job_logglue table
drop table if exists t_job_logglue cascade;
drop sequence if exists seq_t_job_logglue_id cascade;

create sequence seq_t_job_logglue_id increment by 1 minvalue 1
  no maxvalue start with 1;

CREATE TABLE t_job_logglue
(
  id          bigint primary key default nextval('seq_t_job_logglue_id'),
  job_id      bigint      NOT NULL,
  glue_type   varchar(50)         DEFAULT NULL,
  glue_source text,
  glue_remark varchar(128) NOT NULL,
  add_dt      timestamp           DEFAULT NULL,
  last_update_dt timestamp           DEFAULT NULL
);

--5. Create the t_job_registry table
drop table if exists t_job_registry cascade;

CREATE TABLE t_job_registry
(
  id             serial primary key,
  registry_group varchar(50)  NOT NULL,
  registry_key   varchar(255) NOT NULL,
  registry_value varchar(255) NOT NULL,
  last_update_dt timestamp DEFAULT NULL
);


--6. Create the t_job_group table
drop table if exists t_job_group cascade;

CREATE TABLE t_job_group
(
  id           serial      NOT NULL primary key,
  app_name     varchar(64) NOT NULL,
  title        varchar(12) NOT NULL,
  address_type integer     NOT NULL DEFAULT 0,
  address_list text,
  update_time  timestamp            DEFAULT NULL
);

--7. Create the t_job_user table
drop table if exists t_job_user cascade;
CREATE TABLE t_job_user
(
  id         serial     NOT NULL primary key ,
  username   varchar(50) NOT NULL,
  password   varchar(50) NOT NULL ,
  role       integer     NOT NULL ,
  permission varchar(255) DEFAULT NULL
);

--7. Create the t_job_lock
drop table if exists t_job_lock cascade;

CREATE TABLE  t_job_lock  (
   lock_name  varchar(50) NOT NULL primary key
);



