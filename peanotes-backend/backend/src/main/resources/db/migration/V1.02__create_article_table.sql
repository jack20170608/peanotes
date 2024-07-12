drop table if exists t_article cascade;
drop sequence if exists seq_t_article cascade;

create sequence seq_t_article increment by 1 minvalue 1
  no maxvalue start with 1;

create table t_article (
  id NUMERIC(18,0) primary key default nextval('seq_t_article'),
  pid  NUMERIC(18,0) not null ,
  name varchar(256),
  icon varchar(1024),
  tags varchar(2048),
  sort integer default 0,
  cover varchar(256),
  describes varchar(1024),
  star_status integer,
  open_status integer,
  open_version integer,
  pv integer default 0,
  uv integer default 0,
  likes integer default 0,
  words integer default 0,
  version integer default 1,
  color varchar(64),
  toc varchar(128),
  markdown text,
  html text,
  create_dt timestamp,
  update_dt timestamp,
  user_id integer,
  last_edit_time timestamp
);

--The comment on the tables
comment on table t_article is 'All articles.';
comment on column t_article.id is 'The Unique article id';
comment on column t_article.pid is 'The article parent object id';
comment on column t_article.name is 'The article name';
comment on column t_article.icon is 'The article icon';
comment on column t_article.tags is 'The article tags';
comment on column t_article.sort is 'The article sort by';
comment on column t_article.cover is 'The article cover';
comment on column t_article.describes is 'The article describes';


create index idx_t_article_user_id on t_article(user_id);
create index idx_t_article_pid on t_article(pid);

