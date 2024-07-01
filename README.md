# MagMutual User Management System

This repository is a user management system that integrates with a Spring Boot backend and a React frontend. It provides functionalities for user authentication, user CRUD operations, and CSV file uploads.

![AppScreenshot.png](AppScreenshot.png)

## Requirements

Ensure you have the following installed on your system:
- Java 17
- Maven 3.6+
- Node.js 14+
- PostgreSQL 10+

## Setup

### Backend (Spring Boot)

#### Clone the Project

First, clone the repository to your local machine:

```bash
git clone https://github.com/chaichatchai13/magmutual-takehome-project.git
cd magmutual-takehome-project
```
### Configure Database
Ensure PostgreSQL is running and create a database named postgres. The database details and credentials are stored in application.properties. Make sure to configure them according to your local setup.

### Build and Run the Backend
```bash
mvn clean install
mvn spring-boot:run
```

### Frontend (React)
Navigate to the Frontend Directory
```bash
mvn clean install
cd frontend
```

## Install Dependencies
```bash
npm install
```

## Start the Frontend app
```bash
npm start
```

### Running the Application
The backend will be running at http://localhost:8080. 
The frontend will be running at http://localhost:5173.

### API Documentation
Access the Swagger UI for API documentation at:
```bash
http://localhost:8080/swagger-ui.html
```

### Unit Tests
```bash
mvn test
```

### Additional Information
For more details, refer to the comments and documentation within the codebase.