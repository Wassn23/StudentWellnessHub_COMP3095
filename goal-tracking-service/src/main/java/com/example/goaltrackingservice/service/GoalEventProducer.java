package com.example.goaltrackingservice.service;

import com.example.goaltrackingservice.model.GoalCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class GoalEventProducer {

    private final KafkaTemplate<String, GoalCompletedEvent> kafkaTemplate;

    public GoalEventProducer(KafkaTemplate<String, GoalCompletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendGoalCompletedEvent(GoalCompletedEvent event) {
        kafkaTemplate.send("goal-completed-topic", event);
        System.out.println("📤 Sent Kafka event: " + event);
    }
}


