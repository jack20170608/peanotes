drop table if exists t_operation_log cascade;
drop sequence if exists seq_t_operation_log_id cascade;

create sequence seq_t_operation_log_id increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_operation_log(
  id NUMERIC(18,0) primary key default nextval('seq_t_operation_log_id'),
  user_id  integer not null ,
  create_dt timestamp,
  uri varchar(4096),
  details text
);

create index idx_t_operation_log_user_id on  t_operation_log(user_id);

