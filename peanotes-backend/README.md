# peanotes-backend

## Create Database 

```sql
--1. create the app_user
CREATE ROLE app_user WITH
  LOGIN
  SUPERUSER
  CREATEDB
  CREATEROLE
  INHERIT
  NOREPLICATION
  BYPASSRLS
  CONNECTION LIMIT -1
  PASSWORD '1';  
  
--2. create the database
CREATE DATABASE peanotes
    WITH
    OWNER = app_user
    TEMPLATE = template1
    ENCODING = 'UTF8'
    STRATEGY = 'wal_log'
    LC_COLLATE = 'en_US.UTF-8'
    LC_CTYPE = 'en_US.UTF-8'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = 100
    IS_TEMPLATE = False;
    
```
