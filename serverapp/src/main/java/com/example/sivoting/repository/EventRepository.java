package com.example.sivoting.repository;

import com.example.sivoting.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    Optional<Event> findByCode(String code);
    
    List<Event> findByCreatedBy(Long userId);
    
    List<Event> findByIsPublicTrueOrderByStartDateDesc();
    
    List<Event> findByIsActiveTrueAndIsPublicTrueOrderByStartDateDesc();
    
    @Query("SELECT e FROM Event e WHERE e.isPublic = true AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Event> searchPublicEvents(@Param("keyword") String keyword);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.createdBy = :userId")
    Integer countEventsByUser(@Param("userId") Long userId);
}
