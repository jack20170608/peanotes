drop table if exists test cascade;
drop sequence if exists seq_test cascade;

create sequence seq_test increment by 1 minvalue 1
  no maxvalue start with 1;

create table test
(
  id     NUMERIC(22) primary key default nextval('seq_test'),
  others varchar(128)
);
