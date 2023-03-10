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
$ docker run -e AWS_ACCESS_KEY_ID=<aws access key> -e AWS_SECRET_ACCESS_KEY=<aws secret key> -p8080:8080 onlinefilesystem:latest     
```

# Set up AWS Credentials
Set up environment variables:
```bash
$ export AWS_ACCESS_KEY_I=<aws access key>
$ export AWS_SECRET_ACCESS_KEY=<aws secret key>
```
Set up in start up file for future use.

## DEPENDENCY
### Run Mysql using AWS RDS
Add inbound rule with your machine IP on the RDS security page.
- DB Username: admin
- DB Password: admin12345
