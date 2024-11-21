## Install mysql in Rockylinux 9

```shell
## 1. Disable SELinux Permanently
# edit /etc/selinux/config

SELINUX=disabled

## 2. Upgrade system 
sudo dnf update

## 3. 
sudo dnf install mysql-server -y


```

```sql
mysql -u root
CREATE DATABASE test;
CREATE USER 'jack'@'localhost' IDENTIFIED BY '1';
CREATE USER 'jack'@'10.10.10.1' IDENTIFIED BY '1';
       
GRANT ALL PRIVILEGES ON test.* TO 'jack'@'10.10.10.1';
                                        
FLUSH PRIVILEGES;
exit;

mysql --host=10.10.10.10 --user=jack --password=1 test

```

https://reintech.io/blog/setting-up-mysql-database-server-rocky-linux-9
