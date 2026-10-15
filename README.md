# ServiceDesk Lite

A lightweight, console-based IT support ticket management system built
with **Java 21, JDBC, and MySQL**. It demonstrates layered application
design, relational database persistence, automated unit testing,
Docker-based development, and continuous integration with GitHub
Actions.

[![Java
CI](https://github.com/MithulKRS/servicedesk-lite/actions/workflows/ci.yml/badge.svg)](https://github.com/MithulKRS/servicedesk-lite/actions/workflows/ci.yml)

## Features

-   Create support tickets with a title, description, requester, and
    priority.
-   View all tickets and their current status.
-   Search for a ticket by ID.
-   Update ticket status (`OPEN`, `IN_PROGRESS`, or `RESOLVED`).
-   Filter tickets by status or priority.
-   Generate a summary of ticket counts by status.
-   Persist ticket data in MySQL using JDBC.
-   Run unit tests with JUnit 5 and Mockito.
-   Build and run the application with Docker and Docker Compose.
-   Automatically build and test changes using GitHub Actions.

## Tech Stack

  Technology       Purpose
  ---------------- ----------------------------------------------
  Java 21          Application logic and object-oriented design
  JDBC             Database connectivity
  MySQL 8.4        Relational database
  Maven            Dependency management and build
  JUnit 5          Unit testing
  Mockito          Mocking dependencies in tests
  Docker           Containerized application runtime
  Docker Compose   Runs the application and database
  GitHub Actions   Continuous integration

## Architecture

The application separates responsibilities into layers:

-   **Model --- `Ticket.java`:** Represents a support ticket.
-   **DAO --- `TicketDAO.java`:** Performs database operations using
    JDBC.
-   **Service --- `TicketService.java`:** Contains ticket-management
    logic.
-   **Utility --- `DatabaseConnection.java`:** Creates database
    connections.
-   **Entry point --- `Main.java`:** Provides the interactive console
    menu.

``` text
User
 |
 v
Main.java
 |
 v
TicketService
 |
 v
TicketDAO
 |
 v
JDBC
 |
 v
MySQL
```

## Getting Started

### Prerequisites

For the Docker workflow, install [Docker
Desktop](https://www.docker.com/products/docker-desktop/) with Docker
Compose.

For local development and testing, install JDK 21 and [Apache
Maven](https://maven.apache.org/download.cgi).

### Run with Docker

1.  Clone the repository and enter the project directory:

    ``` bash
    git clone https://github.com/MithulKRS/servicedesk-lite.git
    cd servicedesk-lite
    ```

2.  Create a local environment file:

    **PowerShell**

    ``` powershell
    Copy-Item .env.example .env
    ```

    **macOS/Linux**

    ``` bash
    cp .env.example .env
    ```

3.  Open `.env` and set your own values for `DB_PASSWORD` and
    `MYSQL_ROOT_PASSWORD`. Keep `.env` private; do not commit it.

4.  Start the MySQL service:

    ``` bash
    docker compose up -d mysql
    ```

5.  Launch the interactive application:

    ``` bash
    docker compose run --build --rm app
    ```

Use the menu to create, view, search, update, and filter tickets.

**Port conflict:** If port `3306` is already in use on your computer,
change the MySQL host-side port in `compose.yaml` (for example, use
`3307:3306`). Keep the application's internal database host as `mysql`
and its container port as `3306`.

### Run tests locally

From the project directory, run:

``` bash
mvn clean test
```

To package the application:

``` bash
mvn clean package
```

## Testing and Continuous Integration

The current test suite contains **13 unit tests** for `TicketService`.
The local test run completed with 13 tests, zero failures, and zero
errors.

The GitHub Actions workflow is located at `.github/workflows/ci.yml`. It
runs the build-and-test job when code is pushed. The initial workflow
run completed successfully.

These results describe the current test suite and workflow run; they do
not imply exhaustive testing of every input or environment.

## Project Structure

``` text
servicedesk-lite/
├── .github/
│   └── workflows/
│       └── ci.yml
├── src/
│   ├── main/java/com/servicedesk/
│   │   ├── Main.java
│   │   ├── dao/TicketDAO.java
│   │   ├── model/Ticket.java
│   │   ├── service/TicketService.java
│   │   └── util/DatabaseConnection.java
│   └── test/java/com/servicedesk/
│       └── TicketServiceTest.java
├── .env.example
├── .gitignore
├── Dockerfile
├── compose.yaml
├── pom.xml
├── README.md
└── schema.sql
```

## What This Project Demonstrates

-   Layered Java application design and separation of concerns.
-   JDBC-based database access and SQL-backed persistence.
-   Unit testing with JUnit and Mockito.
-   Maven build automation.
-   Docker-based local development.
-   Git version control and GitHub Actions CI.

## Future Improvements

-   Add pagination for large ticket lists.
-   Expand input validation and error handling.
-   Add integration tests against a real MySQL instance.
-   Expose functionality through a Spring Boot REST API.
-   Add authentication, authorization, and audit logging.

## Repository

[github.com/MithulKRS/servicedesk-lite](https://github.com/MithulKRS/servicedesk-lite)
