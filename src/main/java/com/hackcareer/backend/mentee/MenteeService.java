package com.hackcareer.backend.mentee;

import com.hackcareer.backend.common.BadRequestException;
import com.hackcareer.backend.common.NotFoundException;
import com.hackcareer.backend.domain.entity.MenteeProfile;
import com.hackcareer.backend.domain.entity.TaskItem;
import com.hackcareer.backend.domain.entity.TaskReview;
import com.hackcareer.backend.domain.entity.User;
import com.hackcareer.backend.domain.enums.Role;
import com.hackcareer.backend.domain.enums.TaskStatus;
import com.hackcareer.backend.domain.repository.MeetingRepository;
import com.hackcareer.backend.domain.repository.MenteeProfileRepository;
import com.hackcareer.backend.domain.repository.TaskItemRepository;
import com.hackcareer.backend.domain.repository.TaskReviewRepository;
import com.hackcareer.backend.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenteeService {

    private final CurrentUserService currentUserService;
    private final MenteeProfileRepository menteeProfileRepository;
    private final TaskItemRepository taskItemRepository;
    private final TaskReviewRepository taskReviewRepository;
    private final MeetingRepository meetingRepository;

    public MenteeDtos.MenteeProfileResponse getMyProfile() {
        User mentee = currentUserService.currentUser();
        MenteeProfile profile = menteeProfileRepository.findByMenteeId(mentee.getId())
                .orElseThrow(() -> new NotFoundException("Mentee profile not found"));

        return new MenteeDtos.MenteeProfileResponse(
                mentee.getId(),
                mentee.getFullName(),
                mentee.getEmail(),
                profile.getBio()
        );
    }

    public List<MenteeDtos.MenteeTaskResponse> getMyTasks() {
        User mentee = currentUserService.currentUser();
        List<TaskItem> tasks = taskItemRepository.findByMenteeIdOrderByCreatedAtDesc(mentee.getId());
        Map<Long, TaskReview> menteeReviewsByTaskId = new HashMap<>();
        Map<Long, TaskReview> mentorReviewsByTaskId = new HashMap<>();
        taskReviewRepository.findByTaskIdIn(tasks.stream().map(TaskItem::getId).toList())
                .forEach(review -> {
                    if (review.getReviewerRole() == Role.MENTEE) {
                        menteeReviewsByTaskId.put(review.getTask().getId(), review);
                    } else if (review.getReviewerRole() == Role.MENTOR) {
                        mentorReviewsByTaskId.put(review.getTask().getId(), review);
                    }
                });

        return tasks.stream()
                .map(task -> toTaskResponse(
                        task,
                        menteeReviewsByTaskId.get(task.getId()),
                        mentorReviewsByTaskId.get(task.getId())
                ))
                .toList();
    }

    @Transactional
    public MenteeDtos.MenteeTaskResponse completeTask(Long taskId, MenteeDtos.CompleteTaskRequest request) {
        User mentee = currentUserService.currentUser();
        TaskItem task = taskItemRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (!task.getMentee().getId().equals(mentee.getId())) {
            throw new BadRequestException("You are not allowed to complete this task");
        }

        task.setStatus(TaskStatus.DONE);
        if (task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }
        TaskItem saved = taskItemRepository.save(task);
        TaskReview menteeReview = taskReviewRepository.findByTaskIdAndReviewerRole(taskId, Role.MENTEE)
                .orElseGet(() -> TaskReview.builder()
                        .task(saved)
                        .reviewer(mentee)
                        .reviewee(saved.getMentor())
                        .reviewerRole(Role.MENTEE)
                        .build());
        menteeReview.setRating(request.rating());
        menteeReview.setComment(request.comment().trim());
        TaskReview savedMenteeReview = taskReviewRepository.save(menteeReview);
        TaskReview mentorReview = taskReviewRepository.findByTaskIdAndReviewerRole(taskId, Role.MENTOR).orElse(null);

        return toTaskResponse(saved, savedMenteeReview, mentorReview);
    }

    public List<MenteeDtos.MenteeMeetingResponse> getMyMeetings() {
        User mentee = currentUserService.currentUser();
        return meetingRepository.findByMenteeIdOrderByScheduledAtAsc(mentee.getId()).stream()
                .map(meeting -> new MenteeDtos.MenteeMeetingResponse(
                        meeting.getId(),
                        meeting.getMentor().getId(),
                        meeting.getMentor().getFullName(),
                        meeting.getScheduledAt(),
                        meeting.getAgenda(),
                        meeting.getMeetingLink(),
                        meeting.getCreatedAt()
                ))
                .toList();
    }

    private MenteeDtos.MenteeTaskResponse toTaskResponse(TaskItem task, TaskReview menteeReview, TaskReview mentorReview) {
        return new MenteeDtos.MenteeTaskResponse(
                task.getId(),
                task.getMentor().getId(),
                task.getMentor().getFullName(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus().name(),
                task.getCompletedAt(),
                menteeReview != null ? menteeReview.getComment() : null,
                menteeReview != null ? menteeReview.getRating() : null,
                mentorReview != null ? mentorReview.getComment() : null,
                mentorReview != null ? mentorReview.getRating() : null,
                task.getCreatedAt()
        );
    }
}
