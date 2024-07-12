drop table if exists t_article_access_log cascade;
drop sequence if exists seq_t_article_access_log cascade;

create sequence seq_t_article_access_log increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_article_access_log
(
  id         NUMERIC(18, 0) primary key default nextval('seq_t_article_access_log'),
  article_id NUMERIC(18, 0),
  type       integer,
  ip         varchar(128),
  user_agent varchar(256),
  country    varchar(128),
  province   varchar(128),
  city       varchar(128),
  create_dt  timestamp
);




