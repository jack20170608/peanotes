drop table if exists t_article_stat cascade;
drop sequence if exists seq_t_article_stat cascade;

create sequence seq_t_article_stat increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_article_stat
(
  id        NUMERIC(18, 0) primary key default nextval('seq_t_article_stat'),
  type      integer,
  user_id   integer,
  stat_date date,
  stat_value numeric(18,0)
);




