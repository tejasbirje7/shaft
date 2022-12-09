**Features**

User authentication and authorization
Product Catalog
Payment
Shopping Cart
Customer Support
Push Notifications
Shipment Tracking
Comparison Feature


**Layers**

Authorization — responsible for authorizing/managing a user
Presentation — responsible for handling HTTP requests and responding with HTML or JSON/XML
Business logic — the application’s business logic
Database layer — data access objects responsible for accessing the database
Application integration — integration with other services (e.g. via messaging or REST API). Or integration with any other Data sources
Notification module — responsible for sending email/push notifications whenever needed



**Business capabilities**

User Management: This is responsible for user authentication and management through social gateways, manual authentication, email verification and mobile number verification.
Content Management: This is responsible for search bar, navigation and translation services.
Product Catalog: This is responsible for products details, products images and products reviews.
Customer Management: This is responsible for catering facilities like personal subscriptions, saved payment methods, address verification, ship orders, customer credits and loyalty programmes.
Cart Management: This is responsible for shopping cart management, quick order and check out.
Payment Management: This is responsible for processing payment and fraud tracking.
Inventory Management: This is responsible for keep tracking of back orders and pre-orders.
Track Management: This is responsible for keep track of shipment and notifying of orders via push notifications, SMS and email.
Reporting Management: This is responsible for web analytics, business intelligence, products sales reports and many more.
Marketing Engine: This is responsible for personalised marketing and management of recommending productInCarts.



**THINGS TO DO IN PRODUCTION**

#TODO
Insert this setting in database production always to avoid automatic index creation if at all account id is not set
PUT _cluster/settings
{
"persistent": {
"action.auto_create_index": "false"
}
}
