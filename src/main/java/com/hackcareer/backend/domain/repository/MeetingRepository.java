package com.hackcareer.backend.domain.repository;

import com.hackcareer.backend.domain.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByMenteeIdOrderByScheduledAtAsc(Long menteeId);

    List<Meeting> findByMentorIdAndMenteeIdOrderByScheduledAtAsc(Long mentorId, Long menteeId);
}
