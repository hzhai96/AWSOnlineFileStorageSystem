# AWSOnlineFileStorageSystem
Please note: Current AWS access key and secret key has been disabled for security reason. Will keep you update.
## DEPENDENCY
### Run Mysql using AWS RDS
Add inbound rule with your machine IP on the RDS security page.
### Run Mysql locally
Run Mysql with docker
```bash
// For M1 Macs
$ docker run -p 3306:3306 --name dockerMysql -e MYSQL_ROOT_PASSWORD=123 -d arm64v8/mysql:latest
```

```bash
// For Intel chips
$ docker run -p 3306:3306 --name dockerMysql -e MYSQL_ROOT_PASSWORD=123 -d mysql:latest
```

Create Database in MySQL
```sql
CREATE DATABASE file_app;
```
