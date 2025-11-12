package com.example.goaltrackingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class GoalTrackingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoalTrackingServiceApplication.class, args);
    }
}
