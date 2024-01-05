FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/products-catalog.jar /run/products-catalog.jar

ENTRYPOINT java -jar run/products-catalog.jar -D spring.profiles.active=prod -D exec.mainClass="org.shaft.administration.catalog.ProductCatalogApplication"
