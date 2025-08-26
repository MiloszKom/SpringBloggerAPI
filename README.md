# SpringBloggerAPI

SpringBloggerAPI is a simple REST API built with Spring Boot, PostgreSQL, and Docker.
The project simulates a minimal blogging platform with Posts, Comments, and Users, and was created to practice applying familiar backend concepts within the Spring Boot ecosystem.

## 🚀 Live Demo
👉 [SpringBloggerAPI](https://springblogger-app-latest.onrender.com/swagger-ui/index.html)

The deployed version includes interactive API documentation powered by **Swagger UI**. 

## ⚙️ Tech Stack
* Java 17 – Core language for Spring Boot
* Spring Boot – REST API framework
* Spring Security – Authentication and role-based access control
* Hibernate / JPA – Object-relational mapping for PostgreSQL
* PostgreSQL – Relational database for development and production
* AWS RDS – Managed PostgreSQL instance for production
* Docker & Docker Compose – Containerization for app and database
* Maven – Build and dependency management
* JUnit & Spring Test – Unit and integration testing

## 📚 What I Practiced in This Project

Through this project, I practiced applying familiar backend concepts in a Spring Boot environment. I worked on:

* CRUD in Spring Boot → Implemented a Post, Comment and User module with full Create, Read, Update, Delete functionality, using PostgreSQL as the database.
* DTOs and Validation → Applied DTO patterns and input validation in the Spring context.
* Authentication & Authorization → Set up login and registration endpoints, secured protected routes, and added role-based access control.
* Error Handling → Improved error responses and exception management to fit Spring Boot’s best practices.
* Refactoring for Clean Design → Split Post and User DTOs into detailed and summary variants for more efficient API design.
* Testing in Spring Boot → Added initial unit tests and integration tests for posts and users.
* Docker Integration → Containerized the application and database for local dev and production.
* Deployment with AWS RDS → Configured the application to connect to a managed PostgreSQL instance on AWS RDS in production.

## 🛠️ Development Setup

Follow these steps to run the application in **development mode**:

---

### 1. Clone the Repository
```bash
git clone https://github.com/MiloszKom/SpringBloggerAPI.git
```

### 2. Create Environment File
In the project root, create a file named .env.dev.

Example .env.dev file:
```bash
DB_URL=jdbc:postgresql://db:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=password

SPRING_SECURITY_USER_NAME=name
SPRING_SECURITY_USER_PASSWORD=password
```


### 3. Build the Project

Run Maven commands to clean and build the project:

```bash
mvn clean
mvn install
```


### 4. Start Docker Containers

Run the following command to build and start the app along with required services (e.g., database):
```bash
docker-compose -f docker-compose.dev.yml up -d --build
```

### 5. Verify the Setup

Open the app in your browser at:
👉 http://localhost:8080

