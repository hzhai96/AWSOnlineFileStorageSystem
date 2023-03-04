# AWSOnlineFileStorageSystem
Please note: Current AWS access key and secret key has been disabled for security reason. Will keep you update.

# Run in Docker Container
Build:
```bash
$ mvn clean package
$ docker build -t onlinefilesystem .     
```
Run in port 8080:
```bash
$ docker run -p8080:8080 onlinefilesystem:latest     
```
## DEPENDENCY
### Run Mysql using AWS RDS
Add inbound rule with your machine IP on the RDS security page.
- DB Username: admin
- DB Password: admin12345

