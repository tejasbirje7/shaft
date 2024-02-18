FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/customer-management.jar /run/customer-management.jar

ENTRYPOINT java -jar run/customer-management.jar -D exec.mainClass="org.shaft.administration.customermanagement.CustomerManagementApplication"
