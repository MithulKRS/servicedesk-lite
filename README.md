# \# ServiceDesk Lite

# 

# A lightweight, console-based IT support ticket management system built with \*\*Java 21, JDBC, and MySQL\*\*. The project demonstrates object-oriented design, database persistence, automated testing, Docker-based development, and continuous integration with GitHub Actions.

# 

# \## Features

# 

# \- \*\*Create tickets\*\* with a title, description, requester, and priority.

# \- \*\*View all tickets\*\* with their IDs, priorities, statuses, requesters, and creation timestamps.

# \- \*\*Search by ID\*\* to retrieve individual tickets and their descriptions.

# \- \*\*Update ticket status\*\* between `OPEN`, `IN\_PROGRESS`, and `RESOLVED`.

# \- \*\*Filter tickets\*\* by status or priority.

# \- \*\*Generate status summaries\*\* showing ticket counts by status.

# \- \*\*Persistent storage\*\* using MySQL and JDBC.

# \- \*\*Automated tests\*\* using JUnit 5 and Mockito.

# \- \*\*Containerized execution\*\* using Docker and Docker Compose.

# \- \*\*Continuous integration\*\* using GitHub Actions to build and test the project.

# 

# \## Tech Stack

# 

# | Technology | Purpose |

# |---|---|

# | Java 21 | Application logic and object-oriented design |

# | JDBC | Java-to-MySQL database connectivity |

# | MySQL 8.4 | Relational database and persistent storage |

# | Maven | Dependency management and build automation |

# | JUnit 5 | Automated unit testing |

# | Mockito | Mocking dependencies in unit tests |

# | Docker | Containerized application execution |

# | Docker Compose | Coordinating the application and database |

# | GitHub Actions | Automated build-and-test workflow |

# 

# \## Architecture

# 

# The application separates responsibilities into distinct layers:

# 

# \- \*\*Model — `Ticket.java`:\*\* Represents a support ticket and its attributes.

# \- \*\*DAO — `TicketDAO.java`:\*\* Handles database operations using JDBC.

# \- \*\*Service — `TicketService.java`:\*\* Contains ticket-management business logic.

# \- \*\*Utility — `DatabaseConnection.java`:\*\* Manages database connectivity.

# \- \*\*Entry point — `Main.java`:\*\* Provides the interactive console menu.

# 

# \### Request flow

# 

# ```text

# User

# &#x20;|

# &#x20;v

# Main.java

# &#x20;|

# &#x20;v

# TicketService

# &#x20;|

# &#x20;v

# TicketDAO

# &#x20;|

# &#x20;v

# JDBC

# &#x20;|

# &#x20;v

# MySQL Database

# ```

# 

# \## Getting Started

# 

# \### Prerequisites

# 

# For local development, install:

# 

# \- JDK 21

# \- Apache Maven 3.9+

# \- MySQL 8.4, or Docker Desktop with Docker Compose

# 

# \### Option 1: Run with Docker

# 

# 1\. Clone the repository:

# 

# &#x20;  ```bash

# &#x20;  git clone https://github.com/MithulKRS/servicedesk-lite.git

# &#x20;  cd servicedesk-lite

# &#x20;  ```

# 

# 2\. Create your local environment file:

# 

# &#x20;  ```powershell

# &#x20;  Copy-Item .env.example .env

# &#x20;  ```

# 

# 3\. Open `.env` and configure `DB\_PASSWORD` and `MYSQL\_ROOT\_PASSWORD` with your own values.

# 

# 4\. Start the database:

# 

# &#x20;  ```bash

# &#x20;  docker compose up -d mysql

# &#x20;  ```

# 

# 5\. Launch the interactive application:

# 

# &#x20;  ```bash

# &#x20;  docker compose run --build --rm app

# &#x20;  ```

# 

# The application menu lets you create, view, search, update, and filter tickets.

# 

# \*\*Note:\*\* If host port `3306` is already occupied, configure the MySQL port mapping in `compose.yaml` to use an available host port, such as `3307`, while keeping the container port at `3306`.

# 

# \### Option 2: Run tests locally

# 

# Run the unit tests with Maven:

# 

# ```bash

# mvn clean test

# ```

# 

# To package the application:

# 

# ```bash

# mvn clean package

# ```

# 

# \## Testing

# 

# The project currently contains 13 automated unit tests for `TicketService`.

# 

# The local test run completed successfully with:

# 

# \- Tests run: 13

# \- Failures: 0

# \- Errors: 0

# 

# The GitHub Actions `build-and-test` workflow has also completed successfully for the initial push.

# 

# These results reflect the current test suite and workflow run; they do not imply exhaustive testing of every possible input or environment.

# 

# \## Continuous Integration

# 

# The GitHub Actions workflow is located at:

# 

# ```text

# .github/workflows/ci.yml

# ```

# 

# It runs the project's build-and-test process when changes are pushed to the repository. This provides automated verification of the code during development.

# 

# \## Project Structure

# 

# ```text

# servicedesk-lite/

# ├── .github/

# │   └── workflows/

# │       └── ci.yml

# ├── src/

# │   ├── main/

# │   │   └── java/com/servicedesk/

# │   │       ├── Main.java

# │   │       ├── dao/TicketDAO.java

# │   │       ├── model/Ticket.java

# │   │       ├── service/TicketService.java

# │   │       └── util/DatabaseConnection.java

# │   └── test/

# │       └── java/com/servicedesk/

# │           └── TicketServiceTest.java

# ├── .env.example

# ├── .gitignore

# ├── Dockerfile

# ├── compose.yaml

# ├── pom.xml

# ├── README.md

# └── schema.sql

# ```

# 

# \## Learning Outcomes

# 

# This project provided hands-on practice with:

# 

# \- Layered application design and separation of concerns.

# \- Java object-oriented programming.

# \- JDBC, SQL queries, and relational database persistence.

# \- Unit testing with JUnit and Mockito.

# \- Maven build automation and dependency management.

# \- Docker-based development environments.

# \- Git version control and GitHub Actions CI.

# 

# \## Future Improvements

# 

# \- Add pagination for large ticket lists.

# \- Introduce stronger input validation and clearer error messages.

# \- Add integration tests against a real MySQL instance.

# \- Build a REST API using Spring Boot.

# \- Add authentication, authorization, and audit logging.

# 

# \## Repository

# 

# \[GitHub — ServiceDesk Lite](https://github.com/MithulKRS/servicedesk-lite)



