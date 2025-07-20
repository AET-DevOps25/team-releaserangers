## How to Run

1. Start a PostgreSQL container using Docker:

   ```sh
   docker run -p 5432:5432 -e POSTGRES_USER=release -e POSTGRES_PASSWORD=ranger -e POSTGRES_DB=devops25_db -d postgres:15-alpine

2. Navigate to PostgreSQL DB:

   ```sh
   docker exec -it <container_id_or_name> bash
   psql -U <username> -d <database_name>
