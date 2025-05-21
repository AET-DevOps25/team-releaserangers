## How to Run

1. Start a PostgreSQL container using Docker:

   ```sh
   docker run -p 5432:5432 -e POSTGRES_USER=release -e POSTGRES_PASSWORD=ranger -e POSTGRES_DB=devops25_db -d postgres:15-alpine