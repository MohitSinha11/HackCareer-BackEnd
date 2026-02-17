package com.hackcareer.backend.domain.repository;

import com.hackcareer.backend.domain.entity.MenteeProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenteeProfileRepository extends JpaRepository<MenteeProfile, Long> {
    Optional<MenteeProfile> findByMenteeId(Long menteeId);
}
