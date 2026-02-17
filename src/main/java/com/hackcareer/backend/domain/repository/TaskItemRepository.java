package com.hackcareer.backend.domain.repository;

import com.hackcareer.backend.domain.entity.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskItemRepository extends JpaRepository<TaskItem, Long> {
    List<TaskItem> findByMenteeIdOrderByCreatedAtDesc(Long menteeId);

    List<TaskItem> findByMentorIdAndMenteeIdOrderByCreatedAtDesc(Long mentorId, Long menteeId);
}
