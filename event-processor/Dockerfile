FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/event-processor.jar /run/event-processor.jar
COPY keys /tmp/keys

ENTRYPOINT java -jar run/event-processor.jar -D exec.mainClass="org.shaft.administration.eventprocessor.EventProcessorApplication"
