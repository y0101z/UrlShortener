# URL Shortener

 This project depicts the Spring Boot Example of REST API.

## Description

This Project shows the set of web REST methods to shorten long URL using Base 62[a-zA-Z0-9] algorithm .
Using the following endpoints, different operations can be achieved, but only 3 of them considered as main methods :
 - `/create` - This accepts long URL, generates short URL and stores it into MongoDB. HttpStatus returned.
 - `/helloworld/name` - This returns the value of config `config.app.name` from `application.properties`
 - `/users` - This returns the list of Users in the Users table which is created in H2
 - `/users/name/{name}` - This returns the details of the Users passed in URL
 - `/users/load` - Add new users using the Users model. 
    eg. `{
        "name": "Ajay",
        "teamName": "Development",
        "salary": 100
        }`

## Libraries used
 - Spring Boot
 - Spring Configuration
 - Spring REST Controller
 - Spring JPA
 - H2 
 
## Development Tools
 - Git 2.10.0
 - IntelliJ IDEA 2016.2.4
 
## Compilation Command
 - `mvn clean install` - Plain maven clean and install
 
## Deployment steps for Cloud Foundry
 - `cf push spring-boot -p spring-boot-0.0.1-SNAPSHOT.jar`  - Deploy the jar into Cloud Foundry.
 - In case you need to change the buildpacks. Use `-b https://github.com/cloudfoundry/java-buildpack.git#v3.7`