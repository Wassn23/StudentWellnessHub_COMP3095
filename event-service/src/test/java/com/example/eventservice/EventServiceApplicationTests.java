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


}
