FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/marketing-engine.jar /run/marketing-engine.jar

ENTRYPOINT java -jar run/marketing-engine.jar -D exec.mainClass="org.shaft.administration.marketingengine.MarketingEngineApplication"
