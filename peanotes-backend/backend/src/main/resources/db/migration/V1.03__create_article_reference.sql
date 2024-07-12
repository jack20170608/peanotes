drop table if exists t_article_reference cascade;
drop sequence if exists seq_t_article_reference cascade;

create sequence seq_t_article_reference increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_article_reference
(
  id          NUMERIC(18, 0) primary key default nextval('seq_t_article_reference'),
  source_id   NUMERIC(18, 0),
  source_name varchar(256),
  target_id   NUMERIC(18, 0),
  target_name varchar(256),
  target_url  VARCHAR(4096),
  type        integer,
  user_id     integer
);


