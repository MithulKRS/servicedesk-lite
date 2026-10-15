# ServiceDesk Lite

A command-line IT support ticket manager built with **Java 21, JDBC, MySQL, Maven, JUnit 5, Docker, and GitHub Actions**.

## Features

- Create and list support tickets
- Search tickets by ID
- Update ticket status (`OPEN`, `IN_PROGRESS`, `RESOLVED`)
- Filter by status or priority (`LOW`, `MEDIUM`, `HIGH`)
- SQL status summary using `GROUP BY`
- Persist data in MySQL
- Unit tests for service-layer business rules

## Project structure

```text
ServiceDesk/
├── .github/workflows/ci.yml
├── .env.example
├── .gitignore
├── .dockerignore
├── Dockerfile
├── compose.yaml
├── pom.xml
├── schema.sql
└── src/
    ├── main/java/com/servicedesk/
    │   ├── Main.java
    │   ├── model/Ticket.java
    │   ├── dao/TicketDAO.java
    │   ├── service/TicketService.java
    │   └── util/DatabaseConnection.java
    └── test/java/com/servicedesk/TicketServiceTest.java
```

## Prerequisites

- JDK 21
- Maven 3.9+
- Docker Desktop (for the Docker option)

Check installations:

```powershell
java -version
mvn -version
docker --version
docker compose version
```

## Option A — Run with Docker Compose

1. Copy `.env.example` to `.env`.
2. Edit `.env` and set your own local passwords. Do not commit `.env`.
3. Start the application and database:

```powershell
docker compose up --build
```

The console app runs interactively. Use the menu to create/list/search/update/filter tickets and view the summary. Stop it with `Ctrl+C`; then run `docker compose down` to stop the containers.

**Note:** `schema.sql` runs automatically only when MySQL initializes an empty data volume. To reset the demo database completely, run `docker compose down -v` (this deletes stored data), then start again.

## Option B — Run Java locally, with MySQL in Docker

1. Copy `.env.example` to `.env` and set passwords.
2. Start only MySQL:

```powershell
docker compose up -d mysql
docker compose ps
```

Wait for MySQL to become healthy.

3. Set connection variables in the same PowerShell window. Use the same password as `DB_PASSWORD` in `.env`:

```powershell
$env:DB_URL = "jdbc:mysql://localhost:3306/servicedesk?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
$env:DB_USER = "servicedesk_app"
$env:DB_PASSWORD = "your_password_here"
```

4. Build, test, and run:

```powershell
mvn clean verify
java -jar target\servicedesk-lite-1.0.0.jar
```

## Tests

```powershell
mvn test
```

The current unit tests use Mockito to mock `TicketDAO`; they do not require a running database. Database integration itself should be tested locally using Option A or B.

## CI

GitHub Actions runs `mvn --batch-mode verify` on pushes and pull requests and uploads Surefire test reports. This is a **CI build-and-test workflow**, not an automated deployment pipeline.

## Security notes

- `.env` is ignored by Git. Never commit real passwords.
- The example credentials are for local development only.
- This is a learning project; do not expose the database port publicly or use these settings in production.

## Resume bullet (use only after you have run and verified it)

**ServiceDesk Lite | Java, JDBC, MySQL, Maven, JUnit, Docker, GitHub Actions**
- Developed a modular Java ticket-management application with MySQL persistence, JDBC prepared statements, input validation, and SQL-based status reporting.
- Containerized the application and database with Docker Compose and automated Maven build/test execution through GitHub Actions.
