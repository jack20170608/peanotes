drop table if exists t_user_param cascade;
drop sequence if exists seq_t_user_param cascade;

create sequence seq_t_user_param increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_user_param
(
  id          NUMERIC(18, 0) primary key default nextval('seq_t_user_param'),
  user_id     integer,
  param_name  varchar(128),
  param_value varchar(256),
  param_desc  varchar(1024),
  create_dt   timestamp,
  update_dt   timestamp
);




