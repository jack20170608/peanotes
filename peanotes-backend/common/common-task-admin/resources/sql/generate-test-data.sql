-- generate 2k testing data
truncate table t_job_info;

insert into t_job_info( job_group_id
                      , job_desc
                      , add_time
                      , update_time
                      , author
                      , alarm_email
                      , schedule_type
                      , schedule_conf
                      , misfire_strategy
                      , executor_route_strategy
                      , executor_handler
                      , executor_param
                      , executor_block_strategy
                      , executor_timeout
                      , executor_fail_retry_count
                      , glue_type
                      , glue_source
                      , glue_remark
                      , glue_update_time
                      , child_job_id
                      , trigger_status
                      , trigger_last_time
                      , trigger_next_time)
select RANDOM_INTEGER(1, 10)          as job_group_id
     , RANDOM_STR(20, 'abcdefghijk')  as job_desc
     , RANDOM_TIMESTAMP()             as add_time
     , RANDOM_TIMESTAMP()             as update_time
     , RANDOM_STR(8, 'abcde')         as author
     , RANDOM_STR(16, 'abcdefghijk@') as alarm_email
     , 'CRON'                         as schedule_type
     , '5/5 * 0 1 * ?'                as schedule_conf
     , 'DO_NOTHING'
     , 'FIRST'
     , 'demoJobHandler'
     , null
     , 'SERIAL_EXECUTION'
     , 0
     , 0
     , 'BEAN'
     , null
     , 'GLUE代码初始化'
     , RANDOM_TIMESTAMP()             as glue_update_time
     , null
     , 1
     , 0
     , 0
from generate_series(1, 2000);

