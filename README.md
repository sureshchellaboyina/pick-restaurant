# Lunch Preference Application README

## Getting Started

This backend version of the lunch-preference application provides REST API endpoints.

### Running lunch-preference API locally

To run the lunch-preference API locally using Maven, follow these steps:

1. Clone the repository:

   ```shell
   git clone https://github.com/sureshchellaboyina/pick-restaurant.git
   cd pick-restaurant
Run the application with Maven (Make sure to set JAVA_HOME if needed):

shell
Copy code
./mvnw spring-boot:run
API Response
Create Session
Example:

POST http://localhost:8080/api/lunch/create-session

json
Copy code
{
  "teamMember": "Suresh",
  "location": "",
  "sessionName": "Lunch Session 1",
  "initiator": "Suresh"
}
Response: Session created successfully

Invite Users to the Session
POST http://localhost:8080/api/lunch/invite?sessionId={sessionId}&user={user}

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/invite?sessionId=1&user=sam
Response: sam invited to the session.

Joining Session
POST http://localhost:8080/api/lunch/join-session?sessionId={sessionId}&user={user}

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/join-session?sessionId=1&user=sam
Response: sam joined the session.

ErrorResponse: If the user is not invited to join the session but tries to join

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/join-session?sessionId=1&user=peter
Response: peter is not invited to the session.

Submitting Restaurant Choice
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId={sessionId}&user={user}&restaurant={restaurant-choice}

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId=1&user=sam&restaurant=Saizeriya
Response: sam submitted restaurant choice: Saizeriya

ErrorResponse: If the user is trying to submit a restaurant choice without joining the session

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId=1&user=peter&restaurant=KFC
Response: peter is not part of the session and cannot submit a restaurant choice.

ErrorResponse: If the same user is trying to submit a restaurant choice again

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId=1&user=sam&restaurant=mcdonald
Response: sam, you have already submitted a restaurant choice for this session.

Get All Submitted Restaurants
GET http://localhost:8080/api/lunch/get-restaurants?sessionId={sessionId}

Example:

shell
Copy code
GET http://localhost:8080/api/lunch/get-restaurants?sessionId=1
Response:

json
Copy code
[
  "Saizeriya",
  "mcdonald",
  "korean-hotpot",
  "Mrprata",
  "bananaleaf",
  "MrBiryani"
]
End the Session and Pick a Random Restaurant
POST http://localhost:8080/api/lunch/end-session?sessionId={initiator}&user={initiator}

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/end-session?sessionId=1&user=Suresh
Response: Session ended. Selected restaurant: Mrprata

ErrorResponse: If the user is trying to join a session after it has ended

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/join-session?sessionId=1&user=raj
Response: Session has ended and cannot be joined.

ErrorResponse: If the user is trying to submit a restaurant choice after the session has ended

Example:

shell
Copy code
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId=1&user=raj&restaurant=kopitiam
Response: Session has ended, and restaurant choices cannot be submitted.

Test Results
<!-- Include any relevant test results and information here. -->
