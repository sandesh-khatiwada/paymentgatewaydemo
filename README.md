Spring Boot-based Java application designed to demonstrate a simple payment request management system.


Project Structure
The project follows a standard Maven directory structure.


Technologies Used
Java 21: Core programming language.
Spring Boot: Framework for building the REST API.
Spring Data JPA: For database interactions.
MySQL: Relational database for JPA persistence.
JWT-based Authentication: Secures API endpoints using JSON Web Tokens.
MapStruct: For entity-to-DTO and DTO-to-entity mapping.
Lombok: For reducing boilerplate code with annotations.


Dependencies
The following dependencies are used in the project (defined in pom.xml):

Spring Boot Starters:
spring-boot-starter-data-jpa: For JPA-based database operations.
spring-boot-starter-security: For implementing JWT-based authentication.
spring-boot-starter-web: For building RESTful APIs.


MySQL Connector:
mysql-connector-j: For connecting to the MySQL database.


Lombok:
org.projectlombok:lombok: For annotation-based code generation.


JWT:
io.jsonwebtoken:jjwt: For handling JSON Web Tokens.


MapStruct:
org.mapstruct:mapstruct: For entity-DTO mapping.



Prerequisites

Java 21
Maven
MySQL (installed and running)
IDE (e.g., IntelliJ IDEA, Eclipse) with Lombok support enabled








