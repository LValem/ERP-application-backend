# iti0302-2024 project: Kraanaauto ERP

### Project overview

This project aims to build an ERP system tailored for a crane trucking company. The software will streamline the management of incoming jobs, task creation, and assignment to drivers and vehicles. It will also handle scheduling and provide a centralized platform for overseeing various operations, such as tracking driver availability, vehicle status, job progress, and other related data. The goal is to improve efficiency and coordination within the company's logistics and fleet management processes.

### Quicklinks
Project Wiki


## How to run the application (todo):
1. How to run you application
1. How to build it
1. How can you make docker container and how can you run it



## TODO:

**Create Wiki page**

- Document your project idea
- Document used technologies in project
- Document how the CI setup works
- Document first version of your database (needs to have database diagram)

### **DEADLINE 12 OCTOBER**

- [ ] Backend application in git and running
- [ ] At least 4 backend services implemented (can be tested)
- [ ] Project topic discussed with Mentor
- [ ] At least 5 cases/issues defined
- [ ] Backend applications have been deployed to server
- [ ] Application packaged in Docker
- [ ] Reverse proxy setup completed
- [ ] Domain name assigned to server


**General requirements**

- Well defined REST api
    - url-s reference URI-s
    - Http methods (post, get, ...) reference activity
    - Proper status codes
- **The endpoints need to make sense considering the whole application**
- Use JPA
- Use 3 tiered architecture
    - No entity objects used in controller
- Use mapstruct to map entities and dtos
- Use lombok for getters, setters, constructors
- Database schema defined in liquibase
    - Primary key
    - Foreign key
- Proper error handling
    - Exception handler
