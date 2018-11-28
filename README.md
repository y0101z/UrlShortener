# URL Shortener

 This project depicts the Spring Boot Example of REST API.

## Description

This Project shows the set of web REST methods to shorten long URL using Base 62[a-zA-Z0-9] algorithm .
Using the following endpoints, different operations can be achieved, but only 3 of them considered as main methods :
 - `/create` - This accepts long URL, generates short URL, and stores it into MongoDB. HttpStatus returned.
 - `/longurl/{shortUrl}` - This retrieves original long URL from MongoDB, and redirects to original URL
 - `/stats/{shortUrl}` - This returns statistics for the specific short URL clicked.
 
 Helper methods :
 - `/links` - This returns the all link objects from MOngoDBL
 - `/delete/{shorturl}` - Deletes expires links from MongoDB. 
 - `/purgedb` - Deletes all data from  MongoDB.
 - `/init/{n}` - Populates MongoDB with mock data.  
    eg. `{
	  "ShortUrl":"signup",
	  "LongUrl":"https://signup.com/login/signin?triggerUri=%2Fapi%2Forg_activities%2F1420396",
	  "Created":ISODate("2017-10-25T19:03:02.105+0000"),
	  "Expiration":ISODate("2019-11-28T23:27:02.212+0000"),
	  "Clicks":"111"
	},`

## Libraries used
 - Spring Boot
 - Dev Tools
 - Spring Configuration
 - Spring REST Controller
 - Spring JPA
 - MongoDB
 
## Development Tools
 - Git 2.10.0
 - Eclipse Java EE IDE for Web Developers.
 - Version: Oxygen Release (4.7.0)
 
## Compilation Command
 - `mvn clean install` - Plain maven clean and install
 
## Deployment steps for Cloud Foundry
 - `cf push spring-boot -p UrlShortener.jar`  - Deploy the jar into Cloud Foundry.
 - In case you need to change the buildpacks. Use `-b https://github.com/cloudfoundry/UrlShortener.jar.git#v3.7`