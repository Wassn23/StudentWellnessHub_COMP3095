package com.example.goaltrackingservice.model;

public class GoalCompletedEvent {

    private String goalId;
    private String userId;
    private String completedAt;

    public GoalCompletedEvent() {}

    public GoalCompletedEvent(String goalId, String userId, String completedAt) {
        this.goalId = goalId;
        this.userId = userId;
        this.completedAt = completedAt;
    }

    public String getGoalId() { return goalId; }
    public String getUserId() { return userId; }
    public String getCompletedAt() { return completedAt; }

    public void setGoalId(String goalId) { this.goalId = goalId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
}