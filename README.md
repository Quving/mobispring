## API Documentation
#### Environments need to be set.
- GEOFOX_API_USER 
- GEOFOX_API_PASSWORD
- GEOFOX_API_URL

#### DepartureTimes
###### Postrequest without any authentification.
``` curl -X POST -H "Content-Type: application/json" -d '{"station":"Farmsen", "hhMMyyyy":"24.12.2017", "HHmm":"23:24", "maxList":"2"}' http://localhost:8080/api/geofox/departuretime ```

## Installation
- ```./gradlew clean build```
- ```./gradlew bootRun```



