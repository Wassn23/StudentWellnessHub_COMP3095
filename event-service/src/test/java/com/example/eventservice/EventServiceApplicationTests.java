package com.example.eventservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.PostgreSQLContainer;
import org.hamcrest.Matchers;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class EventServiceApplicationTests {

    // use a TestContainer to start up a Postgres instance for testing
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11-alpine");

    // inject random port the test server is running on
    @LocalServerPort
    private Integer port;

    // autowire the WireMockServer to get access to the WireMock runtime info
    @Autowired
    private WireMockServer wireMockServer;

    // setup RestAssured before executing tests
    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    // start the container before tests run
    static {
        postgreSQLContainer.start();
    }

    @Test
    void createEventTest() {

        // sample request body
        String eventJSON = """
                    {
                        "title" : "Test Event Title",
                        "description" : "Test Event Description",
                        "date" : "2024-10-03T09:00:00",
                        "location" : "Toronto",
                        "capacity" : 300                        
                    }
                """;

        var ResponseBodyString = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(eventJSON)
                .when()
                .post("/api/events")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("eventId", Matchers.notNullValue())  // verify ID is generated
                .body("title", Matchers.equalTo("Test Event Title"))
                .body("description", Matchers.equalTo("Test Event Description"))
                .body("date", Matchers.equalTo("2024-10-03T09:00:00"))
                .body("location", Matchers.equalTo("Toronto"))
                .body("capacity", Matchers.equalTo(300));

    }

    private Integer createEventAndReturnId(String title, String description, LocalDateTime date, String location, Integer capacity){

        String requestBody = """
            
                {
                    "title": "%s",
                    "description": "%s",
                    "date": "%s",
                    "location": "%s",
                    "capacity": %d
                }
            """.formatted(title, description, date.toString(), location, capacity);

        System.out.println("Request Body: " + requestBody);  // print the request for debugging

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("api/events")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("eventId");

    }

    @Test
    void getAllEventsTest() {

        createEventAndReturnId("Charity Event", "Charity event for raising awareness for mental health",
                LocalDateTime.of(2000, 4, 12, 9, 0), "Markham", 1000);

        createEventAndReturnId("Marathon", "25km marathon",
                LocalDateTime.of(2013, 3, 9, 9, 0), "Vaughan", 50);

        createEventAndReturnId("Wellness Convention", "Meet wellness gurus and mental health experts",
                LocalDateTime.of(2020, 6, 20, 9, 0), "Toronto", 5000);

        // get all events
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/events")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.greaterThanOrEqualTo(3))
                .body("title", Matchers.hasItems("Charity Event", "Marathon", "Wellness Convention"));
    }

    @Test
    void getUpcomingEventsTest() {

        createEventAndReturnId("Past Event", "This already happened",
                LocalDateTime.of(2020, 1, 1, 10, 0), "Toronto", 50);

        createEventAndReturnId("Future Event 1", "This is upcoming",
                LocalDateTime.now().plusDays(5), "Toronto", 100);

        createEventAndReturnId("Future Event 2", "Also upcoming",
                LocalDateTime.now().plusDays(10), "Vancouver", 75);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/events/upcoming")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("title", Matchers.hasItems("Future Event 1", "Future Event 2"))
                .body("title", Matchers.not(Matchers.hasItem("Past Event")));

    }

    @Test
    void getEventByLocationTest(){

        createEventAndReturnId("Event 4", "Description for event 4",
                LocalDateTime.of(2025, 11, 15, 9, 0), "Toronto", 50);

        createEventAndReturnId("Event 5", "Description for event 5",
                LocalDateTime.of(2025, 11, 15, 9, 0), "Vaughan", 100);

        createEventAndReturnId("Event 6", "Description for event 6",
                LocalDateTime.of(2025, 11, 4, 9, 0), "Toronto", 250);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("location", "Toronto")
                .when()
                .get("/api/events/location/{location}", "Toronto")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.equalTo(2))
                .body("title", Matchers.hasItems("Event 4", "Event 6"))
                .body("location", Matchers.everyItem(Matchers.equalTo("Toronto")));

    }

    @Test
    void updateEventTest() {

        Integer id = createEventAndReturnId("Nutrition 101", "A workshop about the basics of nutrition",
                LocalDateTime.of(2021, 7, 13, 9, 0), "Barrie", 30);

        String requestBody = """
                
                    {
                        "title" : "Nutrition 101",
                        "description" : "An ADVANCED workshop about the basics of nutrition",
                        "date" : "2021-07-13T09:00:00",
                        "location" : "Barrie",
                        "capacity" : 30
                    }
                
                """;

        RestAssured.given()
                .body(requestBody)
                .contentType(ContentType.JSON)
                .when()
                .put("/api/events/{id}", id)
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value());

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/events/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("title", Matchers.equalTo("Nutrition 101"))
                .body("description", Matchers.equalTo("An ADVANCED workshop about the basics of nutrition"))
                .body("location", Matchers.equalTo("Barrie"))
                .body("capacity", Matchers.equalTo(30));

    }

    @Test
    void deleteEventTest() {

        Integer id = createEventAndReturnId("Temp Event", "This is a test event to be disposed",
                LocalDateTime.of(2022, 4, 9, 9, 0), "Mississauga", 500);

        // insert new event
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/events")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("eventId", Matchers.hasItem(id));

        // delete the new event
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/events/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // verify
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/events")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("eventId", Matchers.not(Matchers.hasItem(id)));

    }




}
