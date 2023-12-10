FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/event-ingestion.jar /run/event-ingestion.jar
COPY keys /tmp/keys

ENTRYPOINT java -jar run/event-ingestion.jar -D exec.mainClass="org.shaft.administration.eventingestion.EventIngestionApplication"
