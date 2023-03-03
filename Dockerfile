FROM openjdk:19
COPY target/*.jar file_app.jar
ENTRYPOINT ["java","-jar","/file_app.jar"]