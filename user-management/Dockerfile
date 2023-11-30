FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/user-management.jar /run/user-management.jar
COPY keys /tmp/keys

ENTRYPOINT java -jar run/user-management.jar -D exec.mainClass="org.shaft.administration.usermanagement.UserManagementApplication"
