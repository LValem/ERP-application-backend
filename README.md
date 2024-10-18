# iti0302-2024 backend for Kraanaauto ERP

### Project overview

This project aims to build an ERP system tailored for a crane trucking company. The software will streamline the management of incoming jobs, task creation, and assignment to drivers and vehicles. It will also handle scheduling and provide a centralized platform for overseeing various operations, such as tracking driver availability, vehicle status, job progress, and other related data. The goal is to improve efficiency and coordination within the company's logistics and fleet management processes.

### Quicklinks
[Project wiki](https://gitlab.cs.taltech.ee/hegrun/iti0302-2024-project/-/wikis/Project-wiki)


### How to run the application:
1. Clone the repository
2. Edit the `application.properties` file to access your database
```
DATABASE_URL=jdbc:postgresql://localhost:5432/yourdb
DATABASE_USERNAME=yourusername
DATABASE_PASSWORD=yourpassword
```
3. Run the application with **Gradle**
4. Once the application starts, it will be available at: `http://localhost:8080`

### How to build the application:
1. Use Gradle to clean the project and build the JAR file
```
./gradlew clean build
```
2. Find the built JAR file
3. Run the built JAR. You could use the following command:
```
java -jar your-project-name.jar
```

### How to build and run Docker container:
1. After cloning the repository, you can build the Docker image by running the following command:
```
docker build -t your-docker-username/your-project-name .
```
2. Run the application inside the Docker container
```
docker run -d -p 8080:8080 your-docker-username/your-project-name
```
3. Access the application via Docker at:
```
http://localhost:8080
```
4. Stopping the Docker container
```
docker ps
docker stop <container_id>
```
