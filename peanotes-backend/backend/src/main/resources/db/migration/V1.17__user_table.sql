drop table if exists t_user cascade;
drop sequence if exists seq_t_user cascade;

create sequence seq_t_user increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_user
(
  id        NUMERIC(9, 0) primary key default nextval('seq_t_user'),
  type      integer,
  user_name varchar(128),
  phone     varchar(32),
  password  varchar(512),
  salt      varchar(256),
  nick_name varchar(128),
  real_name varchar(128),
  avatar    varchar(512),
  remark    varchar(1024),
  location  varchar(512),
  create_dt timestamp,
  update_dt timestamp,
  enabled   BOOLEAN                   default true
);




