package com.example.sivoting.repository;

import com.example.sivoting.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    
    List<Candidate> findByEventId(Long eventId);
    
    void deleteByEventId(Long eventId);
}
