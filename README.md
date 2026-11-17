# JobTracker API

A RESTful backend for managing job listings and tracking job applications. Built with Java and Spring Boot, JobTracker provides JWT-based authentication, user-scoped application management, filtering, pagination, and database migrations.

**Live API:** https://job-tracker-v83k.onrender.com  
**Swagger UI:** https://job-tracker-v83k.onrender.com/swagger-ui/index.html  
**Frontend:** https://jobtracker-ajay.vercel.app  
**Frontend repository:** https://github.com/ajayanuragi/job-tracker-frontend

## Tech stack

- **Language:** Java 26
- **Framework:** Spring Boot 4.1.2
- **Security:** Spring Security, JWT (HS256), BCrypt
- **Persistence:** Spring Data JPA, Hibernate, PostgreSQL (Neon)
- **Migrations:** Flyway
- **Build:** Maven (Maven Wrapper)
- **API documentation:** springdoc-openapi / Swagger UI
- **Testing:** JUnit 6, Mockito, Spring MVC and JPA slice tests
- **Hosting:** Render

## Features

- Register and log in with email and password
- Authenticate protected API requests using bearer JWTs
- Create, view, update, and soft-delete job listings
- Browse jobs with pagination and sorting
- Track applications for the authenticated user
- Filter applications by status, company name, and job title
- Paginate and sort application results with validated sort fields
- Prevent duplicate applications for the same user and job
- Validate request payloads and return appropriate HTTP errors
- Manage PostgreSQL schema changes with versioned Flyway migrations
- Explore endpoints using Swagger UI

## API overview

All paths are relative to the API base URL. Unless marked public, endpoints require authentication.

### Authentication

| Method | Endpoint | Description | Access |
| --- | --- | --- | --- |
| `POST` | `/api/users/register` | Register a new account | Public |
| `POST` | `/api/users/login` | Log in and receive a JWT | Public |

### Jobs

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/jobs` | List active jobs (paginated) |
| `GET` | `/api/jobs/{id}` | Get a job by ID |
| `POST` | `/api/jobs` | Create a job |
| `PUT` | `/api/jobs/{id}` | Replace/update a job |
| `PATCH` | `/api/jobs/{id}` | Partially update a job |
| `DELETE` | `/api/jobs/{id}` | Soft-delete a job |

### Applications

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/applications` | List the current user's applications, with optional filters |
| `GET` | `/api/applications/{id}` | Get one of the current user's applications |
| `POST` | `/api/applications` | Apply to a saved job |
| `PATCH` | `/api/applications/{id}` | Update application fields/status |

**Application status values:** `APPLIED`, `INTERVIEW`, `REJECTED`, `OFFER`, `WITHDRAW`.

**Example list request:**

```http
GET /api/applications?page=0&size=10&sort=appliedAt,desc&status=APPLIED&companyName=Acme
Authorization: Bearer <your-jwt>
```

For request and response schemas, use the [interactive Swagger UI](https://job-tracker-v83k.onrender.com/swagger-ui/index.html).

## Authentication and authorization

1. Register using `POST /api/users/register`.
2. Log in using `POST /api/users/login` to obtain a JWT.
3. Include the token on protected requests:

```http
Authorization: Bearer <your-jwt>
```

Passwords are hashed with BCrypt. JWTs use HS256 signing and are validated by Spring Security's OAuth2 resource server support. Sessions are stateless; application queries are scoped to the authenticated user.

The configured token lifetime is **3,600,000 ms (1 hour)**. Expired tokens require a new login.

## Database and migrations

JobTracker uses PostgreSQL, hosted on **Neon** in production. Its main domain entities are:

- **User:** account details and password hash
- **Job:** listing details, employment/workplace types, and a soft-delete flag
- **Application:** links a user to a job, with status, application date, notes, and timestamps

A database-level unique constraint prevents a user from creating multiple applications for the same job. Flyway migrations manage the schema and indexes:

- **V1:** initial schema
- **V2:** indexes

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=validate` so it checks the schema rather than generating or modifying production tables.

## Local setup

### Prerequisites

- JDK 26
- PostgreSQL database (local or hosted)
- Git

### 1. Clone the repository

```bash
git clone https://github.com/ajayanuragi/job-tracker.git
cd job-tracker
```

### 2. Configure environment variables

Set the following variables in your shell or run configuration. **Do not commit real credentials.**

| Variable | Purpose |
| --- | --- |
| `DATABASE_URL` | PostgreSQL JDBC URL, e.g. `jdbc:postgresql://localhost:5432/jobtracker` |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | Base64-encoded random secret of at least 32 decoded bytes |

Example of generating a suitable JWT secret on Linux:

```bash
openssl rand -base64 32
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API runs on `http://localhost:8080` by default. Flyway applies pending migrations on startup; Hibernate validates the resulting schema.

### 4. Open API documentation

Visit `http://localhost:8080/swagger-ui/index.html` to inspect the API and test endpoints. Authenticate with a token from the login endpoint to call protected routes.

## Testing

The backend has **69 passing tests**, covering controller, service, and persistence behavior. The suite uses JUnit 6, Mockito, `@WebMvcTest`, and `@DataJpaTest`.

Run tests with:

```bash
./mvnw test
```

Tests cover authentication, validation, job management, application ownership and filtering, pagination, and error handling.

## Deployment

The backend is deployed on **Render** and connects to **Neon PostgreSQL**. Runtime configuration, including database credentials and the JWT signing secret, is supplied through environment variables.

- **Backend:** https://job-tracker-v83k.onrender.com
- **Frontend (Vercel):** https://jobtracker-ajay.vercel.app

The React frontend communicates with the backend API. Cross-origin requests are controlled by the backend CORS configuration; configure allowed origins for your deployment as appropriate.

## Related project

The [JobTracker frontend](https://github.com/ajayanuragi/job-tracker-frontend) is built with React, TypeScript, Vite, TanStack Query, React Router, and shadcn/ui.

## License

No license is specified here. Refer to the repository for licensing information.
