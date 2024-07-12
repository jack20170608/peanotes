-- generate 2k testing data
truncate table t_operation_log;

insert into t_operation_log (user_id, create_dt, uri, details)
select
  RANDOM_INTEGER(1,10) as user_id
     ,RANDOM_TIMESTAMP() as create_dt
     ,RANDOM_STR() as uri
     ,md5(random()::text) as details
from generate_series(1,2000);

--insert sys param data
insert   into t_sys_param (param_name, param_value, param_desc, create_dt, update_dt)
values ('root_path','peanote','the root path of the peanote app', now(), now())
     , ('dev_platform','java', 'the development platform', now(), now())
     , ('jdk_version', '21', 'jdk version', now(), now())
     , ('app_name', 'peanote', 'the application name', now(), now())
     , ('about', 'application abount', 'peanote is a self manage note tools', now(), now())
