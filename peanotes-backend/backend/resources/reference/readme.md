# 快速生成大量测试数据
```sql

create table people
(
  id        integer,
  name      varchar(32),
  age       integer,
  grade     numeric(4, 2),
  birthday  date,
  logintime timestamp
);

insert into people
select generate_series(1,10000) as id,
md5(random()::text) as name,
(random()*100)::integer as age,
(random()*99)::numeric(4,2) as grade,
now() - ((random()*1000)::integer||' day')::interval as birthday,
clock_timestamp() as logintime;
```

## Reference 
- [PostgreSQL批量生成测试数据](https://www.jianshu.com/p/d465a4c748e8)
- [PostgreSQL批量生成测试数据](https://www.modb.pro/db/608435)
