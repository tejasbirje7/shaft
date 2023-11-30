FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/inventory-management.jar /run/inventory-management.jar

ENTRYPOINT java -jar run/inventory-management.jar -D exec.mainClass="org.shaft.administration.inventory.InventoryManagementApplication"
