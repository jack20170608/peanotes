drop table if exists t_article_recycle cascade;
drop sequence if exists seq_t_article_recycle cascade;

create sequence seq_t_article_recycle increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_article_recycle (
  id NUMERIC(18,0) primary key default nextval('seq_t_article_recycle'),
  pid  NUMERIC(18,0) not null ,
  name varchar(256),
  icon varchar(1024),
  tags varchar(2048),
  sort integer default 0,
  cover varchar(256),
  describes varchar(1024),
  pv integer default 0,
  uv integer default 0,
  likes integer default 0,
  words integer default 0,
  version integer default 1,
  color varchar(64),
  markdown text,
  create_dt timestamp,
  update_dt timestamp,
  delete_dt timestamp,
  user_id integer
);

--The comment on the tables
comment on table t_article_recycle is 'The article recycle.';

create index idx_t_article_recycle_user_id on t_article_recycle(user_id);

