FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/account-management.jar /run/account-management.jar

ENTRYPOINT java -jar run/account-management.jar -D exec.mainClass="org.shaft.administration.accountmanagement.AccountManagementApplication"
