FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/discovery-server.jar /run/discovery-server.jar

ENTRYPOINT java -jar run/discovery-server.jar -D exec.mainClass="org.shaft.administration.discovery-server.AppDiscoveryApplication"
