
# iti0302-2024 Backend repository

### [kallur.servebeer.com](http://kallur.servebeer.com)
### [Frontend repository](https://gitlab.cs.taltech.ee/hegrun/iti0302-2024-frontend)
### [Project wiki](https://gitlab.cs.taltech.ee/hegrun/iti0302-2024-project/-/wikis/Project-wiki)

## Project overview

The aim of this project is to create a **comprehensive ERP system** for a crane trucking company, providing a centralized solution to optimize its operations. The resulting **web application** aims to streamline a variety of logistics tasks, including the management of incoming jobs, task creation, driver and vehicle assignments, and scheduling.

The system should enable the monitoring of **driver availability**, **vehicle status**, **job progress**, and other critical data, improving coordination and decision-making. By automating key processes and providing real-time insights, this ERP system will ultimately enhance the company's **logistics efficiency**, **fleet management**, and **resource allocation**.

### Purpose

The goal of this project is to develop a **user-friendly** web application that can manage complex logistics workflows and provide **real-time tracking** of drivers, vehicles, and jobs. With this tool, the crane trucking company can improve **operational efficiency**, **reduce errors**, and **optimize scheduling**, ultimately driving better service delivery and customer satisfaction.

### Why this project?

The crane trucking industry faces unique challenges that differentiate it from conventional trucking. While traditional trucking often deals with standardized cargo, crane trucking involves transporting irregularly shaped and oversized items, requiring more complex management of cargo space. Unlike simply counting how many pallets fit into a semi-trailer, crane trucking demands a more nuanced approach to planning, scheduling, and resource allocation.

Currently, many crane trucking companies rely on good old pen and paper for managing operations. This method, though easy to implement, results in 
significant manual labor and data entry errors. The need for a technological solution has been confirmed by businesses in the field, 
who recognize the limitations of their current systems. 

This project seeks to modernize operations by automating processes like waybill management, allowing drivers to handle waybills electronically. 

Ultimately, the goal is to increase efficiency, minimize errors, and provide a more seamless way to manage the complexities of crane trucking logistics.

### Key features:
- **Job management**: Create, assign, and track jobs in real-time
- **Driver and vehicle assignment**: Manage available drivers and vehicles for tasks
- **Scheduling**: Plan and optimize job schedules based on resource availability
- **Real-time monitoring**: Track driver location, vehicle status, and job progress
- **Reporting**: Generate reports on job completion, driver performance, and resource utilization


## Technology stack

### Backend:
- **Java 21**
- **Spring Boot** backend framework
- **Spring Security** for authentication and authorization
- **JWT** for token-based authentication
- **Liquibase** for database schema management
- **MapStruct** for Entity-DTO mapping
- **JPA** for database operations
- **PostgreSQL** as database
- **Gradle** for building
- **OpenAPI (Swagger)** for API documentation
- **SLF4J** for logging
- **Global Exception Handler** for centralized error handling
- **BCrypt** for password hashing

### Frontend:
- **React**
- **Nginx**
- **Node.js**

### CI/CD:
- **GitLab CI/CD** for both frontend and backend pipelines

## Prerequisites

Before running this project, make sure you have the following installed:
- **Java 21**
- **Gradle**
- **Docker**
- **Node.js** and **npm**
- **PostgreSQL**

## Setup instructions

### Backend

1. Clone the repository:
   ```
   git clone https://gitlab.cs.taltech.ee/hegrun/iti0302-2024-backend.git
   cd iti0302-2024-backend
   ```

2. Edit the `application.properties` file to configure **your** database connection:
   ```
   DATABASE_URL=jdbc:postgresql://localhost:5432/yourdb
   DATABASE_USERNAME=yourusername
   DATABASE_PASSWORD=yourpassword
   ```

3. Build and run the backend with Gradle:
   ```
   ./gradlew clean build
   java -jar build/libs/your-project-name.jar
   ```

4. Once the application starts, it will be available at: `http://localhost:8080`

### Frontend

1. Clone the frontend repository:
   ```
   git clone https://gitlab.cs.taltech.ee/hegrun/iti0302-2024-frontend.git
   cd iti0302-2024-frontend
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Run the frontend development server:
   ```
   npm start
   ```

   The frontend will be available at: `http://localhost:3000`

### Docker setup

1. Build the Docker image for the backend:
   ```
   docker build -t your-docker-username/your-backend-project .
   ```

1. Build the Docker image for the frontend:
   ```
   docker build -t your-docker-username/your-frontend-project .
   ```

2. Run the backend container:
   ```
   docker run -d -p 8080:8080 your-docker-username/your-backend-project
   ```

2. Run the frontend container:
   ```
   docker run -d -p 3000:80 your-docker-username/your-frontend-project
   ```

3. Access the application via Docker:
* Backend at
   ```
   http://localhost:8080
   ```
* Frontend at
  ```
  http://localhost:3000
  ```

4. Stopping the Docker container:
   ```
   docker ps
   docker stop <container_id>
   ```

### CI/CD Pipelines

- CI/CD pipelines have been set up for both frontend and backend using **GitLab CI/CD**.
- The pipelines automatically build, test, and deploy the application on code changes.

## How to Build the Project

1. **Backend**: Build the backend using Gradle:
   ```
   ./gradlew clean build
   ```

2. **Frontend**: Build the frontend using npm:
   ```
   npm run build
   ```

3. **Docker**: Rebuild the Docker image if you make any changes:
   ```
   docker build -t your-docker-username/your-project-name .
   ```
