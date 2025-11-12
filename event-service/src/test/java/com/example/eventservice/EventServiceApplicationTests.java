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
        RestAssured.baseURI = "http://localhost:";
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
                        "date" : 2024-10-3,
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
                .body("date", Matchers.equalTo(2024 - 10 - 03))
                .body("location", Matchers.equalTo("Toronto"))
                .body("capacity", Matchers.equalTo(300));

    }

    private String createEventAndReturnId(String title, String description, LocalDate date, String location, Integer capacity){

        String requestBody = """
            
                {
                    "title": "%s",
                    "description": "%s",
                    "date": "%s",
                    "location": "%s",
                    "capacity": "%d"
                }
            """.formatted(title, description, date.toString(), location, capacity);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .post("api/events")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("eventId");

    }

    @Test
    void getAllEventsTest() {

        createEventAndReturnId("Charity Event", "Charity event for raising awareness for mental health",
                LocalDate.of(2000, 4, 12), "Markham", 1000);

        createEventAndReturnId("Marathon", "25km marathon",
                LocalDate.of(2013, 3, 9), "Vaughan", 50);

        createEventAndReturnId("Wellness Convention", "Meet wellness gurus and mental health experts",
                LocalDate.of(2020, 6, 20), "Toronto", 5000);

        // get all events
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/events")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.equalTo(3))
                .body("title", Matchers.hasItems("Charity Event", "Marathon", "Wellness Convention"));
    }

    @Test
    void getEventByDateTest() {

        createEventAndReturnId("Event 1", "Description for event 1",
                LocalDate.of(2025, 10, 15), "Toronto", 50);

        createEventAndReturnId("Event 2", "Description for event 2",
                LocalDate.of(2025, 10, 15), "Vaughan", 100);

        createEventAndReturnId("Event 3", "Description for event 3",
                LocalDate.of(2025, 10, 20), "Toronto", 250);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("date", LocalDate.of(2025, 10, 15))
                .when()
                .get("/api/events/{date}")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.equalTo(2))
                .body("title", Matchers.hasItems("Event 1", "Event 2"))
                .body("date", Matchers.everyItem(Matchers.equalTo(LocalDate.of(2025, 10, 15))));

    }

    @Test
    void getEventByLocationTest(){

        createEventAndReturnId("Event 4", "Description for event 4",
                LocalDate.of(2025, 11, 15), "Toronto", 50);

        createEventAndReturnId("Event 5", "Description for event 5",
                LocalDate.of(2025, 11, 15), "Vaughan", 100);

        createEventAndReturnId("Event 6", "Description for event 6",
                LocalDate.of(2025, 11, 4), "Toronto", 250);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("location", "Toronto")
                .when()
                .get("/api/events/location/{location}")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.equalTo(2))
                .body("title", Matchers.hasItems("Event 4", "Event 6"))
                .body("location", Matchers.everyItem(Matchers.equalTo("Toronto")));

    }

    @Test
    void updateEventTest() {

        String id = createEventAndReturnId("Nutrition 101", "A workshop about the basics of nutrition",
                LocalDate.of(2021, 7, 13), "Barrie", 30);

        String requestBody = """
                
                    {
                        "title" : "Nutrition 101",
                        "description" : "A workshop about the basics of nutrition",
                        "date" : 2021-07-13,
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
                .statusCode(HttpStatus.NO_CONTENT.value())
                .header("Location", Matchers.equalTo("http://localhost:" + port + "/api/events/" + id));

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/events/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("find {it.id == '%s' }.title".formatted(id), Matchers.equalTo("Nutrition 101"))
                .body("find {it.id == '%s' }.description".formatted(id), Matchers.equalTo("A workshop about the basics of nutrition"))
                .body("find {it.id == '%s' }.date".formatted(id), Matchers.equalTo(2021-07-13))
                .body("find {it.id == '%s' }.location".formatted(id), Matchers.equalTo("Barrie"))
                .body("find {it.id == '%d' }.capacity".formatted(id), Matchers.equalTo(30));

    }

    @Test
    void deleteEventTest() {

        String id = createEventAndReturnId("Temp Event", "This is a test event to be disposed",
                LocalDate.of(2022, 4, 9), "Mississauga", 500);

        // insert new event
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/events/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.hasItem(id));

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
                .get("/api/events/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.not(Matchers.hasItem(id)));

    }




}
