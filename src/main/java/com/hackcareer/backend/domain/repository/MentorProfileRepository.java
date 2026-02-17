package com.hackcareer.backend.domain.repository;

import com.hackcareer.backend.domain.entity.MentorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MentorProfileRepository extends JpaRepository<MentorProfile, Long> {
    Optional<MentorProfile> findByMentorId(Long mentorId);
}
