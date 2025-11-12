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

import java.time.LocalDate;

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
                .post("/api/goals/")
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

    private String createGoalAndReturnId(String title, String description, LocalDate targetDate, String status, String category) {

        String requestBody = """
                
                    {
                        "title" : "%s",
                        "description" : "%s",
                        "targetDate" : "%s",
                        "status" : "%s",
                        "category" : "%s"
                    }
                
                """.formatted(title, description, targetDate.toString(), status, category);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("api/goals/")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("goalId");

    }

    @Test
    void updateGoalTest(){

        // original product
        String id = createGoalAndReturnId("Stay Hydrated", "Drink at least 2L of water per day",
                LocalDate.of(2025, 3, 20), "in-progress", "nutrition");

        String requestBody = """
                
                    {
                        "title" : "Stay Hydrated",
                        "description" : "Drink at least 3L of water per day",
                        "targetDate" : 2025-03-20,
                        "status" : "in-progress",
                        "category" : "nutrition"
                    }
               
                """;

        RestAssured.given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .put("api/goals/{id}", id)
                .then()
                .log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header("Location", Matchers.equalTo("http://localhost:" + port + "/api/goals/" + id));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/goals/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("find {it.id == '%s' }.title".formatted(id), Matchers.equalTo("Stay Hydrated"))
                .body("find {it.id == '%s' }.description".formatted(id), Matchers.equalTo("Drink at least 3L of water per day"))
                .body("find {it.id == '%s' }.targetDate".formatted(id), Matchers.equalTo(2025-03-20))
                .body("find {it.id == '%s' }.status".formatted(id), Matchers.equalTo("in-progress"))
                .body("find {it.id == '%s' }.category".formatted(id), Matchers.equalTo("nutrition"));

    }

    @Test
    void deleteGoalTest(){

        // original goal
        String id = createGoalAndReturnId("Temp Goal", "This is a test goal to be disposed",
                LocalDate.of(2023, 12, 15), "completed", "exercise");

        // inserted new goal
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/goals/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.hasItem(id));

        // delete new goal
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("api/goals/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // verify deletion
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/goals/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.not(Matchers.hasItem(id)));

    }








}
