**Features**
- User authentication and authorization
- Product Catalog
- Payment
- Shopping Cart
- Customer Support
- Push Notifications
- Shipment Tracking
- Comparison Feature
- Personalise Notification box

**Layers**
- Authorization — responsible for authorizing/managing a user
- Presentation — responsible for handling HTTP requests and responding with HTML or JSON/XML
- Business logic — the application’s business logic
- Database layer — data access objects responsible for accessing the database
- Application integration — integration with other services (e.g. via messaging or REST API). Or integration with any other Data sources
- Notification module — responsible for sending email/push notifications whenever needed

**Business capabilities**
- User Management: This is responsible for user authentication and management through social gateways, manual authentication, email verification and mobile number verification.
- Content Management: This is responsible for search bar, navigation and translation services.
- Product Catalog: This is responsible for products details, products images and products reviews.
- Customer Management: This is responsible for catering facilities like personal subscriptions, saved payment methods, address verification, ship orders, customer credits and loyalty programmes.
- Cart Management: This is responsible for shopping cart management, quick order and check out.
- Payment Management: This is responsible for processing payment and fraud tracking.
- Inventory Management: This is responsible for keep tracking of back orders and pre-orders.
- Track Management: This is responsible for keep track of shipment and notifying of orders via push notifications, SMS and email.
- Reporting Management: This is responsible for web analytics, business intelligence, products sales reports and many more.
- Marketing Engine: This is responsible for personalised marketing and management of recommending productInCarts.

**THINGS TO DO IN PRODUCTION**

#TODO
Insert this setting in database production always to avoid automatic index creation if at all account id is not set
PUT _cluster/settings
{
"persistent": {
"action.auto_create_index": "false"
}
}

**GENERATE DOCKER IMAGES FOR NEW SERVICES**
- Create new file <service-name>.yml in shaft-docker directory
- Copy configuration from one of the yml file from shaft-docker directory to newly created yml file
- Change service name property in yml file
- Change service context property in yml file
- Change service dockerfile property in yml file
- Change service image name property in yml file
- Change ports property in yml file and keep the port as per configured in application.yml file
- Change <build> configuration in pom.xml and replace <mainClass> and <finalName>
- Change elasticsearch host, uris & discovery server details in application.yml file of service
- Add Dockerfile to copy files, dependent jars to docker container from local machine
- Add service to .env file under compose file
- Run mvn clean - mvn compile - mvn install to refresh jars
- Route to shat-docker directory and execute docker compose build
- TO start service execute docker compose up

**SETUP ELASTICSEARCH**
- Install JAVA
  - wget https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
  - tar xvf openjdk-17.0.2_linux-x64_bin.tar.gz
- Set ES_JAVA_HOME in /etc/environment and then do source /etc/environment
- Set `vm.max_map_count=262144` in `/etc/sysctl.conf` then do sysctl -p
- Set JVM memory in /etc/elasticsearch/jvm.options
  - -Xms2g
  - -Xmx2g
- Setup elasticsearch cluster on production referring the below files :
  - shaft-docker/elasticsearch-prod-master.yml
  - shaft-docker/elasticsearch-prod-data-node-1.yml
  - shaft-docker/elasticsearch-prod-data-node-2.yml

**INSTALL DOCKER ON LINUX**
- sudo yum install docker -y
- sudo service docker start
- sudo chkconfig docker on ( make docker autostart )

**INSTALL DOCKER-COMPOSE ON LINUX**
- sudo curl -L https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
- sudo chmod +x /usr/local/bin/docker-compose
- docker-compose version

**CREATE DOCKER NETWORK**
- sudo docker network ls
- sudo docker network create -d bridge shaft-docker_shaft

**SENDGRID ACCESS KEY**
- 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

**SETTING UP KAFKA**
- While setting up kafka - please refer shaft-docker/kafka-cluster-production.yml
- On server keep 2 files common.yml and according to instance it's configuration file.
- For Example - In zookeeper server - keep common.yml file and zookeeper.yml file consisting of configuration from above kafka-cluster-production.yml -> services.zookeeper & services.schema-registry
