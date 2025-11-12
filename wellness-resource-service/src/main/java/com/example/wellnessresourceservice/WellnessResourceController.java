package com.example.wellnessresourceservice;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class WellnessResourceController {

    private final WellnessResourceRepository repository;

    public WellnessResourceController(WellnessResourceRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Cacheable(value = "resources", key = "'all'")
    public List<WellnessResource> getAllResources() {
        System.out.println("Fetching all resources from database...");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @Cacheable(value = "resources", key = "#id")
    public ResponseEntity<WellnessResource> getResourceById(@PathVariable Long id) {
        System.out.println("Fetching resource " + id + " from database...");
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/category/{category}")
    @Cacheable(value = "resources", key = "'category:' + #category")
    public List<WellnessResource> getResourcesByCategory(@PathVariable String category) {
        System.out.println("Fetching resources for category: " + category);
        return repository.findByCategory(category);
    }

    @GetMapping("/search")
    @Cacheable(value = "resources", key = "'search:' + #keyword")
    public List<WellnessResource> searchResources(@RequestParam String keyword) {
        System.out.println("Searching resources with keyword: " + keyword);
        return repository.searchByKeyword(keyword);
    }

    @PostMapping
    @CacheEvict(value = "resources", key = "'all'")
    public ResponseEntity<WellnessResource> createResource(@RequestBody WellnessResource resource) {
        WellnessResource saved = repository.save(resource);
        return ResponseEntity
                .created(URI.create("/api/resources/" + saved.getResourceId()))
                .body(saved);
    }

    @PutMapping("/{id}")
    @CachePut(value = "resources", key = "#id")
    @CacheEvict(value = "resources", key = "'all'")
    public ResponseEntity<WellnessResource> updateResource(
            @PathVariable Long id,
            @RequestBody WellnessResource resource) {

        return repository.findById(id)
                .map(existing -> {
                    existing.setTitle(resource.getTitle());
                    existing.setDescription(resource.getDescription());
                    existing.setCategory(resource.getCategory());
                    existing.setUrl(resource.getUrl());
                    WellnessResource updated = repository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "resources", allEntries = true)
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}