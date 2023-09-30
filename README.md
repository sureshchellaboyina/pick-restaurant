# pick-restaurant
Getting Started
This backend version of the lunch-preference application provides a REST API endpoints.

Running lunch-preference API locally
With maven command line (Set JAVA_HOME if needed)
git clone https://github.com/sureshchellaboyina/pick-restaurant.git
cd pick-restaurant
./mvnw spring-boot:run
API Response
Create Session
Example:
POST http://localhost:8080/api/lunch/create-session

{
"teamMember": "Suresh",
"location": "",
"sessionName": "Lunch Session 1",
"initiator": "Suresh"
}

Session created successfully



invite users to the session
POST http://localhost:8080/api/lunch/invite?sessionId={sessionId}&user={user}

Example:
POST http://localhost:8080/api/lunch/invite?sessionId=1&user=sam

sam invited to the session.

Joining session
POST http://localhost:8080/api/lunch/join-session?sessionId={sessionId}&user={user}

Example:
POST http://localhost:8080/api/lunch/join-session?sessionId=1&user=sam

sam joined the session.
ErrorResponse, if the user is not invited to join the session but user tries to join

Example:
POST http://localhost:8080/api/lunch/join-session?sessionId=1&user=peter

peter is not invited to the session.

submitting restaurant choice
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId={sessionId}&user={user}&restaurant={restaurant-choice}

Example:
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId=1&user=sam&restaurant=Saizeriya

sam submitted restaurant choice: Saizeriya
ErrorResponse, if  user is trying to submit a restaurant choice with out joining the session

Example:
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId=1&user=peter&restaurant=KFC

peter is not part of the session and cannot submit a restaurant choice.
ErrorResponse, if  same user is trying to submit a restaurant choice again

Example:
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId=1&user=sam&restaurant=mcdonald

sam You have already submitted a restaurant choice for this session.

get all the submitted restaurants 
GET http://localhost:8080/api/lunch/get-restaurants?sessionId={sessionId}

Example:
GET http://localhost:8080/api/lunch/get-restaurants?sessionId=1
[
"Saizeriya",
"mcdonald",
"korean-hotpot",
"Mrprata",
"bananaleaf",
"MrBiryani"
]

end the session and pick randomly a restaurant
POST http://localhost:8080/api/lunch/end-session?sessionId={initiator}&user={initiator}

Example:
POST http://localhost:8080/api/lunch/end-session?sessionId=1&user=Suresh
Session ended. Selected restaurant: Mrprata
ErrorResponse, if  user is trying to join a session after session ended

Example:
POST http://localhost:8080/api/lunch/join-session?sessionId=1&user=raj

Session has ended and cannot be joined.
ErrorResponse, if  user is trying to submit a restaurant after session ended

Example:
POST http://localhost:8080/api/lunch/submit-restaurant?sessionId=1&user=raj&restaurant=kopitiam

Session has ended, and restaurant choices cannot be submitted.
Test Results
