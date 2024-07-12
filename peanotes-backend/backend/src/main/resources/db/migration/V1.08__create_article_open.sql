drop table if exists t_article_open cascade;
drop sequence if exists seq_t_article_open cascade;

create sequence seq_t_article_open increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_article_open
(
  id           NUMERIC(18, 0) primary key default nextval('seq_t_article_open'),
  pid          NUMERIC(18, 0),
  words        integer,
  open_version integer,
  open_dt      timestamp,
  sync_dt      timestamp,
  toc          varchar(128),
  markdown     text,
  html         text
);




