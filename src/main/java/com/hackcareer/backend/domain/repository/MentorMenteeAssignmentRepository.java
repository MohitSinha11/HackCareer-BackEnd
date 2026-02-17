package com.hackcareer.backend.domain.repository;

import com.hackcareer.backend.domain.entity.MentorMenteeAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentorMenteeAssignmentRepository extends JpaRepository<MentorMenteeAssignment, Long> {
    boolean existsByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    Optional<MentorMenteeAssignment> findByMentorIdAndMenteeId(Long mentorId, Long menteeId);

    List<MentorMenteeAssignment> findByMentorId(Long mentorId);

    List<MentorMenteeAssignment> findByMenteeId(Long menteeId);
}
