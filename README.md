# Blog Backend Application

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technologies](#technologies)
3. [Installation & Setup](#installation--setup)
4. [Scope of Functionalities](#scope-of-functionalities)
5. [API Endpoints](#api-endpoints)

---

## Introduction

This project is part of a full-stack blogging platform that works in conjunction with a React-based frontend application. <p> The backend serves as the API layer, managing all business logic, data persistence, and security concerns.

---

## Project Overview

### Project

**Blog** is a comprehensive backend API service built with **Java** and **Spring Boot** framework, designed to manage a modern blogging platform.

### Key goals:
- User registry and authentication
- Blog post creation, retrieval, updating, and deletion (CRUD operations)
- Category and tag management for content organization
- RESTful API endpoints for seamless frontend integration

---

## Technologies

### Core Framework
- **Java 23+**: Primary programming language
- **Spring Boot**: Application framework for building production-grade applications
- **Spring Web**: Framework for building RESTful web services and handling HTTP requests
- **Gradle**: Build automation and dependency management tool

### Data Persistence
- **Spring Data JPA**: Object-relational mapping and data access layer
- **Hibernate**: JPA implementation for database operations
- **PostgreSQL**: Relational database management systems (configurable)
- **H2** Relational database management systems configurated for tests

### Security & Authentication
- **Spring Security**: Framework for authentication and authorization
- **JWT (JSON Web Token)**: Token-based authentication for API endpoints

### Additional Technologies
- **Lombok**: Reduces boilerplate code through annotations
- **Mapstruct**: Simplifies DTO to entity conversion
- **Docker & Docker Compose**: Containerization and orchestration

---

## Installation & Setup

### Prerequisites

Before setting up the project, ensure you have the following installed:

- **Java Development Kit (JDK)**: Version 23 or higher
- **Gradle**: Version 8.14.3 or higher
- **Docker & Docker Compose**: For running the PostgreSQL database
- **Git**: For cloning the repository

### Step 1: Clone the Repository

```bash
git clone https://github.com/Stochu/blog.git
cd blog
```

### Step 2: Start the Database with Docker Compose

The project includes a `docker-compose.yml` file that automatically sets up a PostgreSQL database. No manual database creation is needed.

1. Start the database container:

   ```bash
   docker-compose up -d
   ```

   This will:
   - Start a PostgreSQL database on port 5432
   - Start Adminer (database management UI) on port 8888
   - Use the default `postgres` database with password `changemeinprod!`

2. The application is already configured in `src/main/resources/application.properties` to connect to this database:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.username=postgres
   spring.datasource.password=changemeinprod!
   ```

3. (Optional) Access Adminer at `http://localhost:8888` to manage your database visually
   - System: PostgreSQL
   - Server: db
   - Username: postgres
   - Password: changemeinprod!
   - Database: postgres

**Note**: For production environments, make sure to change the default password in both `docker-compose.yml` and `application.properties`.

### Step 3: Configure Application Properties

The `src/main/resources/application.properties` file contains the main configuration. You may want to customize:

```properties
# JWT Configuration
jwt.secret=your_secret_key_here_make_it_long_and_secure
jwt.access-token-expiration=900000
jwt.refresh-token-expiration=86400000

# Logging Configuration
logging.level.com.universalis.blog.security=DEBUG
logging.level.org.springframework.security=DEBUG
```

**For production**, use environment variables:
- `JWT_SECRET`: Your secure JWT secret key
- `JWT_ACCESS_EXPIRATION`: Access token expiration time in milliseconds
- `JWT_REFRESH_EXPIRATION`: Refresh token expiration time in milliseconds

### Step 4: Install Dependencies

```bash
./gradlew build
```

This command will download all required dependencies specified in `build.gradle`.

### Step 5: Run the Application

```bash
./gradlew bootRun
```

Or, build and run as a JAR file:

```bash
./gradlew clean bootJar
java -jar build/libs/blog-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080/api`

---

## Scope of Functionalities

### 1. User Management

#### Registration & Authentication
- User registration with email validation
- Login with JWT token generation
- JWT token refresh mechanism
- Logout functionality

#### User Profile Management
- View user profile information
- Account deactivation

### 2. Blog Post Management

#### Create Operations
- Create new blog posts with rich text content
- Add title, description
- Assign categories and tags to posts
- Draft/publish workflow support

#### Read Operations
- Retrieve all blog posts
- Get individual blog post details
- Filter posts by category, tag

#### Update Operations
- Edit existing blog posts
- Update post content, title
- Change post categories and tags
- Modify publication status

#### Delete Operations
- Delete blog posts
- Delete tags
- Delete categories

### 3. Category & Tag Management

- Create and manage blog categories
- Create and manage blog tags
- Bulk category/tag operations
- Category and tag usage statistics

### 4. Security Features

- JWT-based authentication
- Password encryption
- CORS configuration for frontend integration

---

## API Endpoints

### Authentication Endpoints

```
POST   /api/v1/auth/register       - Register new user
POST   /api/v1/auth/login          - Login user and get JWT token
POST   /api/v1/auth/refresh-token  - Refresh JWT token
POST   /api/v1/auth/logout         - Logout user
```

### Blog Post Endpoints

```
POST   /api/v1/posts                       - Create new blog post
GET    /api/v1/posts                       - Get all posts
GET    /api/v1/posts/{postId}              - Get single post details
PUT    /api/v1/posts/{postId}              - Update blog post
DELETE /api/v1/posts/{postId}              - Delete blog post
GET    /api/v1/posts?categoryId={id}       - Posts by category
GET    /api/v1/posts?tagId={id}            - Posts by tag
GET    /api/v1/posts?categoryId={id}&tagId={id} - Posts by category and tag
```

### Category Endpoints

```
GET    /api/v1/categories              - Get all categories
POST   /api/v1/categories              - Create category
DELETE /api/v1/categories/{categoryId} - Delete category
```

### Tag Endpoints

```
GET    /api/v1/tags                   - Get all tags
POST   /api/v1/tags                   - Create tag
DELETE /api/v1/tags/{tagId}           - Delete tag
```

---

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

---

### Future Enhancements

- pack and deploy with Docker
- roles and permissions management
- Password reset and recovery
- password change
- Update user profile details
- and image to post
- search posts by keyword/title
- full-text search across blog posts - elasticsearch
- sort options (newest, most popular, trending)
- Two factor authentication (2FA)
- like/favorite blog posts
- notifications system
- multi-language support
- add service that modifies pdf files (e.g., merging, splitting, compressing, etc.)
---

**Last Updated**: November 2025
**Version**: 1.0.0
**Maintainer**: Universalis