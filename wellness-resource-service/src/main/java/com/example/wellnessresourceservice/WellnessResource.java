package com.example.wellnessresourceservice;

import jakarta.persistence.*;

@Entity
public class WellnessResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String type;   // e.g., "article", "video", "podcast"
    private String url;

    public WellnessResource() {}

    public WellnessResource(String title, String type, String url) {
        this.title = title;
        this.type = type;
        this.url = url;
    }

    // getters & setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public String getUrl() { return url; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setType(String type) { this.type = type; }
    public void setUrl(String url) { this.url = url; }
}
