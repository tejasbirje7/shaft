FROM openjdk:8-jre-slim
COPY target/dependency-jars /run/dependency-jars
ADD target/cart-management.jar /run/cart-management.jar

ENTRYPOINT java -jar run/cart-management.jar -D exec.mainClass="org.shaft.administration.cartmanagement.CartManagementApplication"
