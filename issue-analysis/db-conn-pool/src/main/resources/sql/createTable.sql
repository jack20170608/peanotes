drop table if exists benchmark_test;

drop sequence if exists seq_benchmark_test_id cascade;

create sequence seq_benchmark_test_id start with 1 increment by 1;

create table benchmark_test
(
  id         bigint primary key default nextval('seq_benchmark_test_id'),
  data       varchar(255) not null,
  value      int          not null,
  created_at timestamp          default current_timestamp
);

insert benchmark_test(data, value)
select trunc(random() * 10000) + 1
from generate_series(1,10000);
