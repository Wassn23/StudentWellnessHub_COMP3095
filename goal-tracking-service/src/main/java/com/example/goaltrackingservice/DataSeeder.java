package com.example.goaltrackingservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDate;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(GoalRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Seeding wellness goals...");

                repository.save(new Goal(
                        "Daily Meditation",
                        "Meditate for 10 minutes every morning",
                        LocalDate.of(2025, 12, 31),
                        "in-progress",
                        "mindfulness"
                ));

                repository.save(new Goal(
                        "Weekly Exercise",
                        "Go to the gym 3 times per week",
                        LocalDate.of(2025, 12, 31),
                        "in-progress",
                        "exercise"
                ));

                repository.save(new Goal(
                        "Healthy Eating",
                        "Eat 5 servings of fruits and vegetables daily",
                        LocalDate.of(2025, 11, 30),
                        "in-progress",
                        "nutrition"
                ));

                repository.save(new Goal(
                        "Better Sleep Schedule",
                        "Sleep 8 hours every night",
                        LocalDate.of(2025, 12, 15),
                        "in-progress",
                        "wellness"
                ));

                repository.save(new Goal(
                        "Stress Management",
                        "Practice breathing exercises when feeling stressed",
                        LocalDate.of(2025, 10, 31),
                        "completed",
                        "mindfulness"
                ));

                System.out.println("Sample goals seeded successfully!");
            }
        };
    }
}