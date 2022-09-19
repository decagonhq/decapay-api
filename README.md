

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

Run the application in a container:
-------------------
	$ docker-compose run --service-ports web -d

 Accessing the Application:
-------------------
Api documentation available at : http://localhost:5000/swagger-ui.html

 Deployment Architecture
-------------------