package com.example.goaltrackingservice.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class ResourceClient {
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Map<String, String>> getResourcesByCategory(String category) {
        // IMPORTANT: use container name, not localhost
        String url = "http://wellness-resource-service:8081/mock-resources/category/" + category;

        ResponseEntity<List<Map<String, String>>> response =
                restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }
}
