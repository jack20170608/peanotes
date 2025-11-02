--Create tables

DROP TABLE IF EXISTS t_order;
DROP SEQUENCE IF EXISTS seq_t_order_id;


CREATE SEQUENCE IF NOT EXISTS seq_t_order_id START 1 ;
CREATE TABLE t_order
(
  id             bigint PRIMARY KEY DEFAULT nextval('seq_t_order_id'),
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


--Generate 1000w data in a sql, should be done in less than 10s
insert into t_order (sequence_no, customer_id, product_id, value_date
                    , price, quality, value, create_dt, last_update_dt)
select gen_random_uuid()
     ,  cast(trunc(random() * 1000) as integer)
     ,  cast(trunc(random() * 10000) as integer)
     ,  date_add(current_date(), cast(trunc(random() * 1000) as integer))
     ,  round(random() * 1000000, 2)
     ,  cast(trunc(random() * 100000) as integer)
     ,  round(random() * 100000000, 2)
     ,  current_localtimestamp()
     ,  current_localtimestamp()
from range(1,10000001);

--Generate 1000w data in a sql
insert into t_order (sequence_no, customer_id, product_id, value_date
         , price, quality, value, create_dt, last_update_dt)
select gen_random_uuid()
     ,  cast(trunc(random() * 1000) as integer)
     ,  cast(trunc(random() * 10000) as integer)
     ,  date_add(current_date(), cast(trunc(random() * 1000) as integer))
     ,  round(random() * 1000000, 2)
     ,  cast(trunc(random() * 100000) as integer)
     ,  round(random() * 100000000, 2)
     ,  current_localtimestamp()
     ,  current_localtimestamp()
from range(1,100000001);
