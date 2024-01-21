Nenad's Notes
=============
### 

Please note that the REST API that implements task's requirements is at:
http://localhost:8080/organisations/{orgId}/orders/{orderId}/shipments

In order to test it in a running service, an order has to be created first using the following API:
http://localhost:8080/organisations/{orgId}/orders/

For details on the API please see http://localhost:8080/swagger-ui/index.html

To run the tests:
```shell
cd <project_root>
docker compose up database -d
gradle flywayMigrate
gradle clean build
docs at -> http://localhost:8080/swagger-ui/index.html
```


