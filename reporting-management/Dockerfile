FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/reporting-management.jar /run/reporting-management.jar

ENTRYPOINT java -jar run/reporting-management.jar -D exec.mainClass="org.shaft.administration.reportingmanagement.ReportingManagementApplication"
