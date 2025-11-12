package com.example.goaltrackingservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalRepository repository;

    public GoalController(GoalRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Goal> getAllGoals() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    public List<Goal> getGoalsByCategory(@PathVariable String category) {
        return repository.findByCategory(category);
    }

    @GetMapping("/status/{status}")
    public List<Goal> getGoalsByStatus(@PathVariable String status) {
        return repository.findByStatus(status);
    }

    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal) {
        if (goal.getStatus() == null || goal.getStatus().isEmpty()) {
            goal.setStatus("in-progress");
        }
        Goal saved = repository.save(goal);
        return ResponseEntity.created(URI.create("/api/goals/" + saved.getGoalId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable String id, @RequestBody Goal goal) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(goal.getTitle());
                    existing.setDescription(goal.getDescription());
                    existing.setTargetDate(goal.getTargetDate());
                    existing.setStatus(goal.getStatus());
                    existing.setCategory(goal.getCategory());
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<Goal> completeGoal(@PathVariable String id) {
        return repository.findById(id)
                .map(goal -> {
                    goal.setStatus("completed");
                    return ResponseEntity.ok(repository.save(goal));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}