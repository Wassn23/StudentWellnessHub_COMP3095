package com.example.wellnessresourceservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/resources")
public class WellnessResourceController {
    private final WellnessResourceRepository repo;

    public WellnessResourceController(WellnessResourceRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<WellnessResource> all() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<WellnessResource> one(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<WellnessResource> create(@RequestBody WellnessResource body) {
        WellnessResource saved = repo.save(body);
        return ResponseEntity.created(URI.create("/resources/" + saved.getId())).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WellnessResource> update(@PathVariable Long id, @RequestBody WellnessResource body) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setTitle(body.getTitle());
                    existing.setType(body.getType());
                    existing.setUrl(body.getUrl());
                    return ResponseEntity.ok(repo.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
