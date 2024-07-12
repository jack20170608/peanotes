drop table if exists t_todo_list cascade;
drop sequence if exists seq_t_todo_list cascade;

create sequence seq_t_todo_list increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_todo_list
(
  id           NUMERIC(18, 0) primary key default nextval('seq_t_todo_list'),
  todo_id      NUMERIC(18, 0),
  todo_name    varchar(128),
  todo_status  integer,
  todo_type    integer,
  task_name    varchar(256),
  task_content text,
  task_tags    varchar(512),
  task_status  varchar(64),
  dead_line    varchar(128),
  start_date   date,
  end_date     date,
  process      integer,
  color        varchar(128),
  user_id      integer,
  create_dt    timestamp
);




