drop table if exists t_note cascade;
drop sequence if exists seq_t_note cascade;

create sequence seq_t_note increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_note
(
  id        NUMERIC(18, 0) primary key default nextval('seq_t_note'),
  top       integer,
  top_dt    timestamp,
  content   varchar(256),
  create_dt timestamp,
  update_dt timestamp,
  user_id   integer
);




