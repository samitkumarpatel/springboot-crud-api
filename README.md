# springboot-crud-api

This repository contains example of a simple Customer CRUD API using Spring Boot, Spring data JDBC & MSSQL.

This repository also showcase How to write integration tests using Testcontainers for data entity, POJO's and router.

## Getting Started

- Start the application in test mode , which will run a local MSSQL container.

```shell
./mvnw clean spring-boot:test-run
```
> The mssql docker image is unix compatible and it will not run on Windows or Mac. use other images for those platforms.

- Test the api
```shell
    http :8080/customer
    http :8080/customer name=Samit email=samit.p@test.net role=Admin
    http :8080/customer
    http :8080/customer/1
    http :8080/customer/1 id=1 name="Samit K" email=samit.patel@test.net role=Admin
    http PUT :8080/customer/1 id=1 name="Samit K" email=samit.patel@test.net role=Admin
    http :8080/customer/1
    http PATCH :8080/customer/1 name="Samit Kumar"
    http :8080/customer/1
    http DELETE :8080/customer/1
    http :8080/customer
    http :8080/customer name=Samit email=samit.p@test.net role=Admin
    http :8080/customer/1
    http PUT :8080/customer/1 name="Samit K" email=samit.patel@test.net role=Admin
    http PATCH :8080/customer/1 name="Samit Kumar"
    http :8080/customer/1
    http DELETE :8080/customer/1
    http :8080/customer/1
    http :8080/customer
```
> Make sure to have `httpie` installed to run the above commands.