package com.hackcareer.backend.domain.repository;

import com.hackcareer.backend.domain.entity.TaskReview;
import com.hackcareer.backend.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskReviewRepository extends JpaRepository<TaskReview, Long> {
    Optional<TaskReview> findByTaskIdAndReviewerRole(Long taskId, Role reviewerRole);

    List<TaskReview> findByTaskIdIn(List<Long> taskIds);
}
