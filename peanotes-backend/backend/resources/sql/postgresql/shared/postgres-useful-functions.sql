--1.0 产生指定范围之间的随机整数（包括边缘）
CREATE OR REPLACE FUNCTION RANDOM_INTEGER(
       START_INT INTEGER default 1, END_INT INTEGER default 100) RETURNS INTEGER AS
$BODY$
DECLARE
BEGIN
RETURN trunc(random() * (END_INT - START_INT + 1) + START_INT);
END;
$BODY$
LANGUAGE plpgsql;
--SELECT RANDOM_INTEGER(1, 10);


--1.1 产生指定日期之间的随机日期
CREATE OR REPLACE FUNCTION RANDOM_DATE(
       START_DATE DATE default '2000-01-01'::DATE,
       END_DATE DATE default '2030-01-01'::DATE ) RETURNS DATE AS
$BODY$
DECLARE
INTERVAL_DAYS INTEGER;
    RANDOM_DAYS INTEGER;
    RANDOM_DATE DATE;
BEGIN
    INTERVAL_DAYS := END_DATE - START_DATE;
    RANDOM_DAYS := RANDOM_INTEGER(0, INTERVAL_DAYS);
    RANDOM_DATE := START_DATE + RANDOM_DAYS;
RETURN RANDOM_DATE;
-- 返回时间戳
-- RETURN date_part('year', RANDOM_DATE) * 10000 +
-- date_part('month', RANDOM_DATE) * 100 +
-- date_part('day', RANDOM_DATE);
END;
$BODY$
LANGUAGE plpgsql;
-- SELECT RANDOM_DATE('2017-01-01', '2017-10-27');

--1.2 产生指定长度的随机字符串
CREATE OR REPLACE FUNCTION RANDOM_STR(
  num INTEGER default 8,
  chars TEXT default '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'
) RETURNS TEXT
LANGUAGE plpgsql
AS $BODY$
DECLARE
res_str TEXT := '';
BEGIN
  IF num < 1 THEN
      RAISE EXCEPTION 'Invalid length';
END IF;
FOR __ IN 1..num LOOP
    res_str := res_str || substr(chars, floor(random() * length(chars))::int + 1, 1);
END LOOP;
RETURN res_str;
END $BODY$;
-- select RANDOM_STR(10) ;

--1.3 生成指定范围的时间戳
CREATE OR REPLACE FUNCTION RANDOM_TIMESTAMP(start_time timestamp default date_trunc('year', now()), end_time timestamp default now())
RETURNS TIMESTAMP
LANGUAGE PLPGSQL
as $BODY$
begin
return start_time + round((extract(epoch from end_time)- extract(epoch from start_time))* random()) * interval '1 second';
end;
$BODY$ ;
-- select RANDOM_TIMESTAMP() ;


