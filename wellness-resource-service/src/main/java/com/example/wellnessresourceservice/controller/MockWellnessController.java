package com.example.wellnessresourceservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wellness")
public class MockWellnessController {

    @GetMapping("/category/{category}")
    public List<Map<String, String>> getMockResources(@PathVariable String category) {
        Map<String, String> resource = new HashMap<>();
        resource.put("id", "1");
        resource.put("title", "Mindfulness 101");
        resource.put("url", "https://example.com/mindfulness");
        resource.put("category", category);

        return Collections.singletonList(resource);
    }
}
