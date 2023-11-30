FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/app-gateway.jar /run/app-gateway.jar
COPY keys /tmp/keys

ENTRYPOINT java -jar run/app-gateway.jar -D exec.mainClass="org.shaft.administration.appgateway.AppGatewayApplication"
