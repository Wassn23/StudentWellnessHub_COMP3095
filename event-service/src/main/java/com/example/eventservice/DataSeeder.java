package com.example.eventservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(EventRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Seeding wellness events...");

                repository.save(new Event(
                        "Yoga and Mindfulness Workshop",
                        "Join us for a relaxing yoga session focused on mindfulness and stress relief",
                        LocalDateTime.of(2025, 12, 15, 14, 0),
                        "Casa Loma Campus - Gym",
                        30
                ));

                repository.save(new Event(
                        "Mental Health Awareness Seminar",
                        "Learn about mental health resources and coping strategies",
                        LocalDateTime.of(2025, 12, 20, 10, 0),
                        "St. James Campus - Room 301",
                        50
                ));

                repository.save(new Event(
                        "Nutrition and Wellness Talk",
                        "Discover healthy eating habits for student life",
                        LocalDateTime.of(2025, 11, 25, 13, 0),
                        "Waterfront Campus - Cafeteria",
                        40
                ));

                repository.save(new Event(
                        "Meditation Circle",
                        "Weekly meditation session for stress management",
                        LocalDateTime.of(2025, 11, 18, 17, 30),
                        "Casa Loma Campus - Wellness Center",
                        20
                ));

                System.out.println("Sample events seeded successfully!");
            }
        };
    }
}