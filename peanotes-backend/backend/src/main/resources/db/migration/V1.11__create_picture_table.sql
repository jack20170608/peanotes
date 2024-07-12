drop table if exists t_picture cascade;
drop sequence if exists seq_t_picture cascade;

create sequence seq_t_picture increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_picture
(
  id          NUMERIC(18, 0) primary key default nextval('seq_t_picture'),
  pid         NUMERIC(18, 0),
  name        varchar(256),
  source_name varchar(256),
  path_name   varchar(512),
  url         varchar(4096),
  rate        integer,
  star_status integer,
  suffix      varchar(64),
  size        integer,
  create_dt   timestamp,
  update_dt   timestamp,
  user_id     integer
);




