drop table if exists t_web_collect cascade;
drop sequence if exists seq_t_web_collect cascade;

create sequence seq_t_web_collect increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_web_collect
(
  id        NUMERIC(18, 0) primary key default nextval('seq_t_web_collect'),
  name      varchar(128),
  url       varchar(4096),
  icon      varchar(256),
  image     varchar(256),
  type      varchar(64),
  sort      integer,
  create_dt timestamp,
  user_id   integer
);




