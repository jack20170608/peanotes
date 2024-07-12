drop table if exists t_article_log cascade;
drop sequence if exists seq_t_article_log cascade;

create sequence seq_t_article_log increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_article_log
(
  id         NUMERIC(18, 0) primary key default nextval('seq_t_article_log'),
  article_id NUMERIC(18, 0),
  version    integer,
  markdown   text,
  create_dt  timestamp
);


