package com.example.wellnessresourceservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WellnessResourceRepository extends JpaRepository<WellnessResource, Long> {

    List<WellnessResource> findByCategory(String category);

    @Query("SELECT r FROM WellnessResource r WHERE " +
            "LOWER(r.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<WellnessResource> searchByKeyword(@Param("keyword") String keyword);
}