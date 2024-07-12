drop table if exists t_folder cascade;
drop sequence if exists seq_t_folder cascade;

create sequence seq_t_folder increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_folder
(
  id           NUMERIC(18, 0) primary key default nextval('seq_t_folder'),
  pid          NUMERIC(18, 0),
  name varchar(256),
  icon varchar(256),
  tags varchar(256),
  star_status integer,
  open_status integer,
  sort integer,
  cover varchar(256),
  color varchar(64),
  describes varchar(4096),
  store_path varchar(512),
  subject_words integer,
  subject_update_dt timestamp,
  type integer,
  create_dt timestamp,
  update_dt timestamp,
  user_id integer
);




