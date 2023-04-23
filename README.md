# money-manager

Multipurpose application for managing my money.

- process monthly bank statements and apply rules to categorize transactions
- track past transactions and create spending reports
- plan future transactions and create budget reports
- track assets and liabilities

Run docker-compose with postgres database, redis, java application and react frontend:
- run 'mvn spring-boot:build-image' to build docker java backend
- run 'docker build . -t money-manager-react' inside frontend root directory to build react frontend
- run 'docker-compose up --build -d' to start all containers

Run docker container with postgres database:
docker run -d -p 5432:5432 --name moneymng --restart always -e POSTGRES_USER=moneymng -e POSTGRES_PASSWORD=moneymng -e
POSTGRES_DB=moneymng -v moneymng_postgres_data:/var/lib/postgresql/data postgres

Run docker container with Redis and Redis Insight:
docker run -d -p 6378:6379 -p 8001:8001 --name redis-stack --restart always redis/redis-stack:latest

Run docker container with Money Manager:
mvn spring-boot:build-image
docker run -d -p 8088:8088 --name moneymng --restart always -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/moneymng -e SPRING_DATASOURCE_USERNAME=moneymng -e SPRING_DATASOURCE_PASSWORD=moneymng docker.io/library/money-manager:0.0.1-SNAPSHOT

Main features:
- Each request has its own request id which is propagated into logs, async tasks and response headers
- Each request is logged with whole request and response using Logbook
  - needs https://github.com/zalando/logbook spring boot 3 update
- Switch between classic and Json output of the log by feature flag - feature.toggles.logging.output.json=true
- Swagger documentation
- Integration tests using Testcontainers and Rest-assured
- Performance tests using Gatlin
  - To run performance tests, firstly start application and then run 'mvn gatling:test'
  - Check HTML report in the target/gatling directory
- Integration with Google Sheets API via http interface
- Caching data using Redis - asynchronous saving

TODO backlog:
- IMPORT:
  - upload button in import page which will accept csv file with transactions
  - response from parsed transactions should return list of successfully parsed transactions and list of errors
  - upload button in import page which will accept json configuration
- EXPENSES:
  - create bar chart for spending by expense nature (must, need, want)
- COMMON:
  - import transactions should use stepper https://mui.com/material-ui/react-stepper/ , first step is upload csv file,
    second step is validate data, show errors and update data, third step is save data
  - create table for cash flow
  - create table for assets and liabilities
  - create line chart for predicting future balance, income and expenses
  - add icons to categories
  - add expense type by nature (must, need, want)
  - add transaction type (Direct debit, Standing order, Regular payment, Subscription, One-time payment)
  - create docker-compose with database, backend and frontend
  - use exchange service to convert currencies when creating transactions
  - use spring profiles
- BUGFIXES:
  - http://localhost:8088/categories?category=null
- TECHNOLOGIES:
  - unleash
  - keycloak