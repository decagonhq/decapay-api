

## Decapay Backend API

### Overview

Decapay is a digital product that allows users to track and manage their finances.

### Features:
- Manage Budgets
- Manage Budget categories.
- Manage Budget line items.
- log and manage Expenses.
- Track expenses.

### Tools:
- Language/Framework: Java/Spring Boot
- Flyway -Database migration
- JUnit - Unit and Integration testing
- AWS SMTP -Email notification service
- Postgres -RDBMS
- Github Actions
- Docker
- AWS Elastic Container Repository(ECR)
- AWS Elastic Container Service (ECS)



To build the application:
-------------------
From the command line:

	$ mvn clean install

Run the application:
-------------------
From the command line:

    $ mvn spring-boot:run

From the IDE:

    Run the main method in the com.decagon.decapay.DecapayApplication class from your IDE.

Run the application in a container:
-------------------
	$ docker-compose run --service-ports app -d

To speed up the Project development, a `docker-compose` file has been provided that
will run the application and a [PostgreSQL](https://www.postgresql.org/) instance,
which you can start with the following command: `docker-compose up`. You can connect to
this instance from your application with the following values:

```
JDBC URL = jdbc:postgresql://localhost:5432/decapay
Username = decapay
Password = password
```
Run the Tests:
-------------------
From the command line:

    $ mvn test

From the IDE:
    
        Run the test classes in the com.decagon.decapay package within the test package from your IDE.


Accessing the Application:
-------------------
Api documentation available at : http://localhost:5000/swagger-ui.html


Deployment Architecture
-------------------