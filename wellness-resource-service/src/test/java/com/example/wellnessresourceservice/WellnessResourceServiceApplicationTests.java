package com.example.wellnessresourceservice;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.hamcrest.Matchers;
import static io.restassured.RestAssured.given;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WellnessResourceServiceApplicationTests {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11-alpine");

    @ServiceConnection
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2.1").withExposedPorts(6379);

    // inject random port for these tests
    @LocalServerPort
    private Integer port;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {

        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

        // clear all caches before each test
        cacheManager.getCacheNames().forEach(cacheName ->
                cacheManager.getCache(cacheName).clear()
        );
    }

    // start Postgres container
    static{
        postgreSQLContainer.start();
        redisContainer.start();
    }

    private String createResourceAndReturnId(String title, String description, String category, String url) {

        String requestBody = """
                
                    {
                        "title" : "%s",
                        "description" : "%s",
                        "category" : "%s",
                        "url" : "%s"
                    }
                
                """.formatted(title, description, category, url);

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("api/resources")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .extract()
                .path("resourceId");
    }

    @Test
    void createResourceTest(){

        String requestBody = """
                
                    {
                        "title" : "Mental Health Hotline",
                        "description" : "24/7 crisis support line",
                        "category" : "counseling",
                        "url" : "https://example.com/hotline"
                    }
              
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("api/resources")
                .then()
                .log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("resourceId", Matchers.notNullValue())
                .body("title", Matchers.equalTo("Mental Health Hotline"))
                .body("description", Matchers.equalTo("24/7 crisis support line"))
                .body("category", Matchers.equalTo("crisis management"))
                .body("url", Matchers.equalTo("https://example.com/hotline"));

    }

    @Test
    void getAllResourcesTest(){

        createResourceAndReturnId("Meditation Guide", "Step by step guide for meditation", "mindfulness", "https://example.com/meditation");
        createResourceAndReturnId("Find a Therapist", "Find local therapists", "counseling", "https://example.com/therapy");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/resources")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.equalTo(2))
                .body("title", Matchers.hasItems("Meditation Guide", "Find a Therapist"));

    }

    @Test
    void getResourcesByCategoryTest(){

        createResourceAndReturnId("Group Counselling", "Counselling for groups and couples", "counseling", "https://example.com/counseling1");
        createResourceAndReturnId("Mindful Thinking", "Step by step guide for meditation", "mindfulness", "https://example.com/meditation");
        createResourceAndReturnId("Virtual Counselling", "Online therapy", "counseling", "https://example.com/counseling2");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("category", "counseling")
                .when()
                .get("api/resources")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", Matchers.equalTo(2))
                .body("title", Matchers.hasItems("Group Counselling", "Virtual Counselling"))
                .body("category", Matchers.everyItem(Matchers.equalTo("counseling")));

    }

    @Test
    void searchResourceByKeywordTest(){

        createResourceAndReturnId("Anxiety Support Group", "Weekly meetings for anxiety management", "counseling", "https://example.com/anxiety");
        createResourceAndReturnId("Depression Resources", "Self-help materials", "counseling", "https://example.com/depression");
        createResourceAndReturnId("Mindfulness Techniques", "Mindfulness techniques and tips", "mindfulness", "https://example.com/mind");

        RestAssured.given()
                .contentType(ContentType.JSON)
                .queryParam("keyword", "anxiety")
                .when()
                .get("api/resources/search")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body("title", Matchers.hasItems("Anxiety Support Group"));

    }

    @Test
    void updateResourceTest(){

        String id = createResourceAndReturnId("Suicide Prevention Hotline", "24/7 help", "counseling", "https://example.com/hotline");

        String requestBody = """
                
                    {
                        "title" : "Suicide Prevention Hotline",
                        "description" : "24/7 help with multilingual services",
                        "category" : "counseling",
                        "url" : "https://example.com/hotline-updated"
                    }
                
                """;

        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/resources/{id}", id)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("title", Matchers.equalTo("Suicide Prevention Hotline"))
                .body("description", Matchers.equalTo("24/7 help with multilingual services"))
                .body("category", Matchers.equalTo("counseling"))
                .body("url", Matchers.equalTo("https://example.com/hotline-updated"));

    }

    @Test
    void deleteResourceTest(){

        String id = createResourceAndReturnId("Temp Resource", "Resource to be disposed of", "mindfulness", "https://example.com/temp");

        // insert temp resource
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/resources/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.hasItem(id));

        // delete temp resource
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .delete("api/resources/{id}", id)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // verify deletion
        RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get("api/resources/")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("resourceId", Matchers.not(Matchers.hasItem(id)));

    }

}
