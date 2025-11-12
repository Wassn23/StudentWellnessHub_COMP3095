package com.example.wellnessresourceservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(WellnessResourceRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                System.out.println("Seeding wellness resources...");

                repository.save(new WellnessResource(
                        "Campus Counseling Services",
                        "Free, confidential counseling for all GBC students",
                        "counseling",
                        "https://www.georgebrown.ca/wellness/counseling"
                ));

                repository.save(new WellnessResource(
                        "Mindfulness Meditation Guide",
                        "Learn meditation techniques to reduce stress and anxiety",
                        "mindfulness",
                        "https://www.georgebrown.ca/wellness/meditation"
                ));

                repository.save(new WellnessResource(
                        "Exercise and Fitness Programs",
                        "Free fitness classes and gym access for students",
                        "exercise",
                        "https://www.georgebrown.ca/wellness/fitness"
                ));

                repository.save(new WellnessResource(
                        "Nutrition Counseling",
                        "Get personalized nutrition advice from registered dietitians",
                        "nutrition",
                        "https://www.georgebrown.ca/wellness/nutrition"
                ));

                repository.save(new WellnessResource(
                        "Mental Health Crisis Line",
                        "24/7 crisis support hotline for students in distress",
                        "counseling",
                        "https://www.georgebrown.ca/wellness/crisis"
                ));

                repository.save(new WellnessResource(
                        "Stress Management Workshop",
                        "Learn practical strategies to manage academic stress",
                        "mindfulness",
                        "https://www.georgebrown.ca/wellness/stress"
                ));

                System.out.println("Sample data seeded successfully!");
            }
        };
    }
}