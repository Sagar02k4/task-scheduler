# Task Scheduler API

A backend system for managing tasks with dependency resolution, priority-based scheduling, and deadline conflict detection — built to solve the same core problem real project-management tools (Jira, Asana) solve internally: figuring out the correct order to execute a set of interdependent tasks.

**Live API Docs (Swagger UI):** https://task-scheduler-7ial.onrender.com/swagger-ui/index.html


## What It Does

Tasks can depend on other tasks (e.g., "Backend Setup" depends on "Database Design"). The system:

- Detects and rejects circular dependencies before they're created
- Computes a valid execution order using topological sort
- Within that order, prioritizes tasks by importance (HIGH/MEDIUM/LOW) whenever multiple tasks are simultaneously ready — without ever violating a dependency
- Flags scheduling conflicts when the same person has multiple tasks due the same day
- Enforces that a task can only move to IN_PROGRESS/DONE once all its dependencies are DONE

## Tech Stack

- **Java 21 / Spring Boot** — REST API, layered architecture (Controller → Service → Repository)
- **PostgreSQL (Supabase)** — persistence via Spring Data JPA / Hibernate
- **Spring Security + JWT** — stateless authentication
- **JUnit 5 + Mockito** — unit tests for core business logic
- **springdoc-openapi (Swagger)** — interactive API documentation
- **Docker** — containerized for deployment
- **Render** — hosting

## Core Algorithms

| Feature | Data Structure / Algorithm |
|---|---|
| Cycle detection | DFS-based graph traversal |
| Execution ordering | Topological sort |
| Priority scheduling | Kahn's algorithm (in-degree tracking) + PriorityQueue (max-heap via custom comparator) |
| Deadline conflicts | HashMap-based grouping by (assignee, date) |

## Features

- JWT-based user registration and login
- Full CRUD for tasks (title, description, priority, status, deadline, assignee)
- Task ownership — each task belongs to the user who created it; users can only view/modify their own tasks
- Many-to-many self-referencing task dependencies
- Cycle detection on dependency creation
- Topological sort endpoint (plain execution order)
- Priority-aware topological sort endpoint (execution order respecting priority when multiple tasks are ready)
- Deadline conflict detection across assignees
- Status transitions blocked until dependencies are complete
- Paginated task listing
- Request validation (`@Valid`) with clean, field-level error responses
- Global exception handling (cycle errors, validation errors, malformed JSON) mapped to proper HTTP status codes
- Sensitive fields (password hashes) excluded from all API responses
- Unit tests covering cycle detection, topological sort, and priority-vs-dependency edge cases

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/register` | Register a new user |
| POST | `/auth/login` | Log in, returns a JWT |
| POST | `/tasks` | Create a task |
| GET | `/tasks?page=&size=` | List the logged-in user's tasks (paginated) |
| GET | `/tasks/{id}` | Get a task by ID |
| DELETE | `/tasks/{id}` | Delete a task |
| POST | `/tasks/{taskId}/dependencies/{dependsOnId}` | Add a dependency |
| GET | `/tasks/execution-order` | Get topologically sorted execution order |
| GET | `/tasks/priority-execution-order` | Get execution order respecting priority |
| GET | `/tasks/deadline-conflicts` | List detected deadline conflicts |
| PATCH | `/tasks/{id}/status?status=` | Update task status |

All `/tasks/**` endpoints require a JWT (`Authorization: Bearer <token>`), obtained from `/auth/login`, and are scoped to the authenticated user — a user can only see or modify tasks they created.

## Running Locally

**Prerequisites:** Java 21, Maven, a PostgreSQL database

1. Clone the repo
   ```
   git clone https://github.com/Sagar02k4/task-scheduler
   cd task-scheduler
   ```

2. Set the following environment variables (or configure them in your IDE's run configuration):
   ```
   DB_URL=jdbc:postgresql://<host>:<port>/<database>
   DB_USERNAME=<your-username>
   DB_PASSWORD=<your-password>
   ```

3. Run
   ```
   ./mvnw spring-boot:run
   ```

4. Open `http://localhost:8080/swagger-ui/index.html`

## Running Tests

```
./mvnw test
```

## What I'd Add Next

- Pagination on `GET /tasks`
- Role-based access control (e.g., only assignees can update their own tasks)
- A small frontend to visualize the dependency graph