package com.example.goaltrackingservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MongoDBContainer;
import org.hamcrest.Matchers;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GoalTrackingServiceApplicationTests {

    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @LocalServerPort
    private Integer port;

    // this will execute before each integration test
    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    static {
        mongoDBContainer.start();
    }

    @Test
    void createGoalTest(){

        String requestBody = """
                
                    {
                        "title" : "Daily Outdoor Walks",
                        "description" : "Go for a walk outside every day for at least 30 minutes",
                        "targetDate" : 2025-06-18,
                        "status" : "in-progress",
                        "category" : "exercise"
                    }
                
                """;

        RestAssured.given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .post("/api/goals/create")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("goalId", Matchers.notNullValue())
                .body("title", Matchers.equalTo("Daily Outdoor Walks"))
                .body("description", Matchers.equalTo("Go for a walk outside every day for at least 30 minutes"))
                .body("targetDate", Matchers.equalTo(2025-06-18))
                .body("status", Matchers.equalTo("in-progress"))
                .body("category", Matchers.equalTo("exercise"));
    }








}
