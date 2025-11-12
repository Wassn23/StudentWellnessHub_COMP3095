package com.example.eventservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository repository;

    public EventController(EventRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Event> getAllEvents() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{location}")
    public List<Event> getEventsByLocation(@PathVariable String location) {
        return repository.findByLocation(location);
    }

    @GetMapping("/upcoming")
    public List<Event> getUpcomingEvents() {
        return repository.findUpcomingEvents(LocalDateTime.now());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody Event event) {
        Event saved = repository.save(event);
        return ResponseEntity.created(URI.create("/api/events/" + saved.getEventId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(event.getTitle());
                    existing.setDescription(event.getDescription());
                    existing.setDate(event.getDate());
                    existing.setLocation(event.getLocation());
                    existing.setCapacity(event.getCapacity());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<?> registerStudent(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String studentId = body.get("studentId");
        if (studentId == null || studentId.isEmpty()) {
            return ResponseEntity.badRequest().body("studentId is required");
        }

        return repository.findById(id)
                .map(event -> {
                    if (event.isFull()) {
                        return ResponseEntity.badRequest().body("Event is full");
                    }
                    if (event.isStudentRegistered(studentId)) {
                        return ResponseEntity.badRequest().body("Student already registered");
                    }
                    event.getRegisteredStudents().add(studentId);
                    Event updated = repository.save(event);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/unregister")
    public ResponseEntity<?> unregisterStudent(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String studentId = body.get("studentId");
        if (studentId == null || studentId.isEmpty()) {
            return ResponseEntity.badRequest().body("studentId is required");
        }

        return repository.findById(id)
                .map(event -> {
                    if (!event.isStudentRegistered(studentId)) {
                        return ResponseEntity.badRequest().body("Student not registered for this event");
                    }
                    event.getRegisteredStudents().remove(studentId);
                    Event updated = repository.save(event);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}