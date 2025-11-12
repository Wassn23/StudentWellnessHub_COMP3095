package com.example.eventservice;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private Integer capacity;

    @ElementCollection
    @CollectionTable(name = "event_registrations", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "student_id")
    private List<String> registeredStudents = new ArrayList<>();

    public Event() {}

    public Event(String title, String description, LocalDateTime date, String location, Integer capacity) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.capacity = capacity;
    }

    // Getters and Setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public List<String> getRegisteredStudents() { return registeredStudents; }
    public void setRegisteredStudents(List<String> registeredStudents) { this.registeredStudents = registeredStudents; }

    // Helper methods
    public int getAvailableSpots() {
        return capacity - registeredStudents.size();
    }

    public boolean isFull() {
        return registeredStudents.size() >= capacity;
    }

    public boolean isStudentRegistered(String studentId) {
        return registeredStudents.contains(studentId);
    }
}