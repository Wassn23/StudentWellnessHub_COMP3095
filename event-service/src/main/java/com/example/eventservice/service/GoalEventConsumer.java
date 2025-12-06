package com.example.eventservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class GoalEventConsumer {

    @KafkaListener(topics = "goal_completed", groupId = "event-service")
    public void listen(Object message) {
        System.out.println("🔥 Received GoalCompletedEvent → " + message);
    }
}
