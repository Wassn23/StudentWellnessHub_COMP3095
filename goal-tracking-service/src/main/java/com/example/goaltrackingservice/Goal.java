package com.example.goaltrackingservice;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "goals")
public class Goal {

    @Id
    private String goalId;
    private String title;
    private String description;
    private LocalDate targetDate;
    private String status;
    private String category;

    public Goal() {}

    public Goal(String title, String description, LocalDate targetDate, String status, String category) {
        this.title = title;
        this.description = description;
        this.targetDate = targetDate;
        this.status = status;
        this.category = category;
    }

    public String getGoalId() { return goalId; }
    public void setGoalId(String goalId) { this.goalId = goalId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
