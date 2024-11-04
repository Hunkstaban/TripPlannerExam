# TASK ANSWERS
## Task 3.3.2 Endpoint output
### NB: At this task I had missed the category enums in the beginning of the document, so I created my own. This gets fixed, and the populator also uses the correct categories.
### 1. GET http://localhost:7070/api/v1/trips
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 09:36:02 GMT
Content-Type: application/json
Content-Length: 674

[
{
"id": 1,
"starttime": "2024-06-01T09:00:00",
"endtime": "2024-06-01T17:00:00",
"startposition": "Mountain Base",
"name": "Mountain Adventure",
"price": 150.0,
"category": "ADVENTURE"
},
{
"id": 2,
"starttime": "2024-08-15T08:00:00",
"endtime": "2024-08-15T14:00:00",
"startposition": "Forest Entrance",
"name": "Forest Hike",
"price": 120.0,
"category": "WILDLIFE"
},
{
"id": 3,
"starttime": "2024-07-10T10:00:00",
"endtime": "2024-07-10T16:00:00",
"startposition": "City Center",
"name": "City Exploration",
"price": 100.0,
"category": "CULTURAL"
},
{
"id": 4,
"starttime": "2024-09-05T11:00:00",
"endtime": "2024-09-05T18:00:00",
"startposition": "Sunny Beach",
"name": "Beach Relaxation",
"price": 80.0,
"category": "RELAXATION"
}
]

### 2. GET http://localhost:7070/api/v1/trips/1 
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 09:36:30 GMT
Content-Type: application/json
Content-Length: 171

{
"id": 1,
"starttime": "2024-06-01T09:00:00",
"endtime": "2024-06-01T17:00:00",
"startposition": "Mountain Base",
"name": "Mountain Adventure",
"price": 150.0,
"category": "ADVENTURE"
}

### 3. POST http://localhost:7070/api/v1/trips
HTTP/1.1 201 Created
Date: Mon, 04 Nov 2024 09:36:46 GMT
Content-Type: application/json
Content-Length: 171

{
"id": 5,
"starttime": "2024-06-01T09:00:00",
"endtime": "2024-06-01T17:00:00",
"startposition": "Mountain Base",
"name": "Mountain Adventure",
"price": 150.0,
"category": "ADVENTURE"
}

### 4. PUT http://localhost:7070/api/v1/trips/1
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 09:47:08 GMT
Content-Type: application/json
Content-Length: 173

{
"id": 1,
"starttime": "2024-07-01T09:00:00",
"endtime": "2024-07-01T17:00:00",
"startposition": "Updated Location",
"name": "Updated Adventure",
"price": 200.0,
"category": "ADVENTURE"
}

### 5. DELETE http://localhost:7070/api/v1/trips/1
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 09:51:30 GMT
Content-Type: text/plain
Content-Length: 22

Trip with id 1 deleted

### 6. PUT http://localhost:7070/api/v1/trips/2/guides/2
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 09:55:35 GMT
Content-Type: text/plain
Content-Length: 48

Guide with ID 2 has been added to Trip with ID 2

### 7. GET http://localhost:7070/api/v1/trips/2
#### After changing showing guide information
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 10:01:22 GMT
Content-Type: application/json
Content-Length: 295

{
"id": 2,
"starttime": "2024-08-15T08:00:00",
"endtime": "2024-08-15T14:00:00",
"startposition": "Forest Entrance",
"name": "Forest Hike",
"price": 120.0,
"category": "WILDLIFE",
"guide": {
"id": 2,
"firstname": "Jane",
"lastname": "Smith",
"email": "jane.smith@example.com",
"phone": "987654321",
"yearsOfExperience": 8
}
}


### 8. POST http://localhost:7070/api/v1/trips/populate

HTTP/1.1 201 Created
Date: Mon, 04 Nov 2024 09:43:20 GMT
Content-Type: text/plain
Content-Length: 36

Database populated with sample data.

## TASK 3.3.5 Question answer
We use PUT to add a guide to a trip because it aligns with updating an existing resource (the Trip), rather than creating a new one. PUT is idempotent, so repeated requests don’t alter the outcome. POST is typically reserved for creating new resources, which isn’t the case here.

## TASK 8.3 Question answer
When adding security roles to endpoints, tests will return a `401 Unauthorized` response if they do not include a valid JWT token.
In order to fix this we need to:
1. Generate JWT Token for testing

2. Add Authorization Header in Tests: For each test that requires authentication, we need to include an `Authorization` header with the token:
   ```java
   given()
       .header("Authorization", "Bearer " + userToken / adminToken 
       .when()
       .get("/protected-endpoint")
       .then()
       .statusCode(200);
   
## KNOWN OMMISSIONS
To make sure that the additional information on trips like guides and package items is only displayed when using the /trips/{id} endpoint, a separate DTO should be implemented. 
Currently, all information will be returned, as it's set up on the TripDTO and through the package controller methods. 