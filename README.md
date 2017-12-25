# What is Mobispring?
It provides a RestAPI to retrieve informations regarding the transport-network in Hamburg/Germany using the framework [Spring](https://projects.spring.io/spring-boot/).
To be able hosting it by yourself, you need to request an access to the Geofox-API](http://www.geofox.de/).
___
## API Documentation / Usage

#### DepartureTimes
###### Postrequest without any authentification.
``` curl -X POST -H "Content-Type: application/json" -d '{"station":"Farmsen", "hhMMyyyy":"24.12.2017", "HHmm":"23:24", "maxList":"2"}' http://localhost:8080/api/geofox/departuretime ```
___
## Installation
- ```./gradlew clean build```
- ```./gradlew bootRun```
___
## Environments need to be set.
- GEOFOX_API_USER 
- GEOFOX_API_PASSWORD
- GEOFOX_API_URL


