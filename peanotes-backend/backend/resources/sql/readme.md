# 性能测试

```sql
--一条sql语句产生1亿的测试数据 postgres
insert into t_order (sequence_no, customer_id, product_id, value_date, price,
                     quality, value, create_dt, last_update_dt)
select gen_random_uuid()
     , trunc(random() * 1000) + 1
     , trunc(random() * 10000) + 1
     , current_date + cast(trunc(random()) * 1000 as integer)
     , round( cast( random() * 1000000 as numeric), 2)
     , cast(trunc(random() * 100000) as integer)
     , round( cast( random() * 100000000 as numeric), 2)
     , current_timestamp + ( cast(trunc(random()) * 2000 as integer) * interval '1 day') + ( cast(trunc(random()) * 2000000 as integer) * interval '1 second')
     , current_timestamp + ( cast(trunc(random()) * 2000 as integer) * interval '1 day') + ( cast(trunc(random()) * 2000000 as integer) * interval '1 second')
from generate_series(1,100000000);

```



```sql
--一条sql语句产生1亿的测试数据 duckdb 
insert into t_order (sequence_no, customer_id, product_id, value_date, price, quality, value, create_dt, last_update_dt)
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
```
