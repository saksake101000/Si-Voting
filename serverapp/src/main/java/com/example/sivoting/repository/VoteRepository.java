package com.example.sivoting.repository;

import com.example.sivoting.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    Optional<Vote> findByUserIdAndEventId(Long userId, Long eventId);
    
    Boolean existsByUserIdAndEventId(Long userId, Long eventId);
    
    List<Vote> findByEventId(Long eventId);
    
    List<Vote> findByUserId(Long userId);
    
    Long countByEventId(Long eventId);
    
    Long countByCandidateId(Long candidateId);
    
    @Query("SELECT v.candidateId, COUNT(v) FROM Vote v WHERE v.eventId = :eventId GROUP BY v.candidateId")
    List<Object[]> countVotesByCandidateForEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT COUNT(DISTINCT v.userId) FROM Vote v WHERE v.eventId = :eventId")
    Integer countUniqueVotersByEvent(@Param("eventId") Long eventId);
    
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.userId = :userId")
    Integer countVotesByUser(@Param("userId") Long userId);
}
