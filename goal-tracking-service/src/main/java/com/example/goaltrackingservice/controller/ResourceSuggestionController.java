package com.example.goaltrackingservice.controller;

import com.example.goaltrackingservice.service.ResourceClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goals")
public class ResourceSuggestionController {

    private final ResourceClient resourceClient;

    public ResourceSuggestionController(ResourceClient resourceClient) {
        this.resourceClient = resourceClient;
    }

    @GetMapping("/{category}/suggested-resources")
    public List<Map<String, String>> getSuggestedResources(@PathVariable String category) {
        return resourceClient.getResourcesByCategory(category);
    }
}
