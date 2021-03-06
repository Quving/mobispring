[![Build Status](http://drone.vingu.online/api/badges/Quving/mobispring/status.svg)](http://drone.vingu.online/Quving/mobispring)

# What is Mobispring?
It provides a RestAPI to retrieve informations regarding the transport-network in Hamburg/Germany using the framework [Spring](https://projects.spring.io/spring-boot/).
To be able hosting it by yourself, you need to request an access to the [Geofox-API](http://www.geofox.de/).
___
## API Documentation / Usage

#### DepartureTimes
###### Postrequest without any authentification.
``` curl -X POST -H "Content-Type: application/json" -d '{"station":"Farmsen", "hhMMyyyy":"24.12.2017", "HHmm":"23:24", "maxList":"2"}' http://localhost:8080/api/geofox/departuretime ```
___
## Installation
###### Install docker and run the application:
- ``` docker pull pingu/mobispring ```
- ``` docker run -d -e GEOFOX_API_USER='user123' -e GEOFOX_API_PASSWORD='passw123' -e GEOFOX_API_URL='http://api.endpoint/' -p 8080:8080 pingu/mobispring```


###### Install java and gradle, build and host it by yourself.
- ```./gradlew clean build```
- ```./gradlew bootRun```
___
## Environments need to be set.
- GEOFOX_API_USER 
- GEOFOX_API_PASSWORD
- GEOFOX_API_URL


