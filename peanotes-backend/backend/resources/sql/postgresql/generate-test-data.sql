-- generate 2k testing data
truncate table t_operation_log;

insert into t_operation_log (user_id, create_dt, uri, details)
select RANDOM_INTEGER(1, 10) as user_id
     , RANDOM_TIMESTAMP()    as create_dt
     , RANDOM_STR()          as uri
     , md5(random()::text)   as details
from generate_series(1, 2000);

--insert sys param data
insert into t_sys_param (param_name, param_value, param_desc, create_dt,
                         update_dt)
values ('root_path', 'peanote', 'the root path of the peanote app', now(),
        now())
     , ('dev_platform', 'java', 'the development platform', now(), now())
     , ('jdk_version', '21', 'jdk version', now(), now())
     , ('app_name', 'peanote', 'the application name', now(), now())
     , ('about', 'application abount', 'peanote is a self manage note tools',
        now(), now())


--Generate testing data for postgres
DROP TABLE IF EXISTS t_order;
DROP SEQUENCE IF EXISTS seq_t_order_id;

CREATE SEQUENCE IF NOT EXISTS seq_t_order_id START 1;

CREATE TABLE t_order
(
  id             BIGINT PRIMARY KEY DEFAULT nextval('seq_t_order_id'),
  sequence_no    VARCHAR(64) NOT NULL,
  customer_id    INTEGER     NOT NULL,
  product_id     INTEGER     NOT NULL,
  value_date     DATE,
  price          DECIMAL,
  quality        INTEGER     NOT NULL,
  value          DECIMAL,
  create_dt      TIMESTAMP,
  last_update_dt TIMESTAMP
);

create index idx_t_order_value_date on t_order(value_date);
create index idx_t_order_price on t_order(price);
create index idx_t_order_quality on t_order(quality);


insert into t_order (sequence_no, customer_id, product_id, value_date, price,
                     quality, value, create_dt, last_update_dt)
select gen_random_uuid()
     , RANDOM_INTEGER(1, 1000)
     , RANDOM_INTEGER(1, 10000)
     , RANDOM_DATE('2000-01-01','2026-01-01')
     , round( cast( random() * 1000000 as numeric), 2)
     , cast(trunc(random() * 100000) as integer)
     , round( cast( random() * 100000000 as numeric), 2)
     , RANDOM_TIMESTAMP('2020-01-01 00:00:00', '2026-01-01 00:00:00' )
     , RANDOM_TIMESTAMP('2021-01-01 00:00:00', '2026-01-01 00:00:00' )
from generate_series(1,10000000);






