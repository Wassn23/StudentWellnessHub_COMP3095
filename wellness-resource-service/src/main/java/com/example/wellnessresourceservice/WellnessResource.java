package com.example.wellnessresourceservice;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "wellness_resources")
public class WellnessResource implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resourceId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String url;

    public WellnessResource() {}

    public WellnessResource(String title, String description, String category, String url) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.url = url;
    }

    public Long getResourceId() { return resourceId; }
    public void setResourceId(Long resourceId) { this.resourceId = resourceId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}