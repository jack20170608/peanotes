drop table if exists t_plan cascade;
drop sequence if exists seq_t_plan cascade;

create sequence seq_t_plan increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_plan
(
  id            NUMERIC(18, 0) primary key default nextval('seq_t_plan'),
  group_id      NUMERIC(18, 0),
  user_id       integer,
  type          integer,
  title         varchar(256),
  content       text,
  plan_month    varchar(8),
  plan_date     date,
  plan_start_dt timestamp,
  plan_end_dt   timestamp,
  color         varchar(64),
  position      varchar(256),
  image         varchar(512)
);




