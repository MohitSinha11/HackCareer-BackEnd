package com.hackcareer.backend.mentor;

import com.hackcareer.backend.common.BadRequestException;
import com.hackcareer.backend.common.NotFoundException;
import com.hackcareer.backend.domain.entity.*;
import com.hackcareer.backend.domain.enums.Role;
import com.hackcareer.backend.domain.enums.TaskStatus;
import com.hackcareer.backend.domain.repository.*;
import com.hackcareer.backend.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MentorService {

    private final CurrentUserService currentUserService;
    private final MentorMenteeAssignmentRepository assignmentRepository;
    private final UserRepository userRepository;
    private final TaskItemRepository taskItemRepository;
    private final TaskReviewRepository taskReviewRepository;
    private final MeetingRepository meetingRepository;
    private final MentorProfileRepository mentorProfileRepository;

    public List<MentorDtos.MenteeSummary> getAssignedMentees() {
        User mentor = currentUserService.currentUser();
        return assignmentRepository.findByMentorId(mentor.getId()).stream()
                .map(a -> new MentorDtos.MenteeSummary(
                        a.getMentee().getId(),
                        a.getMentee().getFullName(),
                        a.getMentee().getEmail()
                ))
                .distinct()
                .toList();
    }

    @Transactional
    public MentorDtos.TaskResponse createTask(MentorDtos.CreateTaskRequest request) {
        User mentor = currentUserService.currentUser();
        User mentee = validateAssignedMentee(mentor, request.menteeId());

        TaskItem task = taskItemRepository.save(TaskItem.builder()
                .mentor(mentor)
                .mentee(mentee)
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .build());

        return new MentorDtos.TaskResponse(
                task.getId(),
                mentor.getId(),
                mentee.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getStatus().name(),
                task.getCompletedAt(),
                null,
                null,
                null,
                null,
                task.getCreatedAt()
        );
    }

    @Transactional
    public MentorDtos.MeetingResponse createMeeting(MentorDtos.CreateMeetingRequest request) {
        User mentor = currentUserService.currentUser();
        User mentee = validateAssignedMentee(mentor, request.menteeId());

        Meeting meeting = meetingRepository.save(Meeting.builder()
                .mentor(mentor)
                .mentee(mentee)
                .scheduledAt(request.scheduledAt())
                .agenda(request.agenda())
                .meetingLink(request.meetingLink())
                .build());

        return new MentorDtos.MeetingResponse(
                meeting.getId(),
                mentor.getId(),
                mentee.getId(),
                meeting.getScheduledAt(),
                meeting.getAgenda(),
                meeting.getMeetingLink(),
                meeting.getCreatedAt()
        );
    }

    public MentorDtos.MentorProfileResponse getMyProfile() {
        User mentor = currentUserService.currentUser();
        MentorProfile profile = mentorProfileRepository.findByMentorId(mentor.getId())
                .orElseThrow(() -> new NotFoundException("Mentor profile not found"));

        return new MentorDtos.MentorProfileResponse(
                mentor.getId(),
                mentor.getFullName(),
                mentor.getEmail(),
                profile.getAbout(),
                profile.getReview(),
                profile.getRating()
        );
    }

    public List<MentorDtos.TaskResponse> getTasksByMentee(Long menteeId) {
        User mentor = currentUserService.currentUser();
        User mentee = validateAssignedMentee(mentor, menteeId);
        List<TaskItem> tasks = taskItemRepository.findByMentorIdAndMenteeIdOrderByCreatedAtDesc(mentor.getId(), mentee.getId());
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
    public MentorDtos.TaskResponse reviewTaskForMentee(Long taskId, MentorDtos.ReviewTaskRequest request) {
        User mentor = currentUserService.currentUser();
        TaskItem task = taskItemRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        if (!task.getMentor().getId().equals(mentor.getId())) {
            throw new BadRequestException("You are not allowed to review this task");
        }

        if (task.getStatus() != TaskStatus.DONE) {
            throw new BadRequestException("Task must be completed by mentee before mentor review");
        }

        TaskReview mentorReview = taskReviewRepository.findByTaskIdAndReviewerRole(taskId, Role.MENTOR)
                .orElseGet(() -> TaskReview.builder()
                        .task(task)
                        .reviewer(task.getMentor())
                        .reviewee(task.getMentee())
                        .reviewerRole(Role.MENTOR)
                        .build());
        mentorReview.setRating(request.rating());
        mentorReview.setComment(request.comment().trim());
        TaskReview savedMentorReview = taskReviewRepository.save(mentorReview);
        TaskReview menteeReview = taskReviewRepository.findByTaskIdAndReviewerRole(taskId, Role.MENTEE).orElse(null);

        return toTaskResponse(task, menteeReview, savedMentorReview);
    }

    public List<MentorDtos.MeetingResponse> getMeetingsByMentee(Long menteeId) {
        User mentor = currentUserService.currentUser();
        User mentee = validateAssignedMentee(mentor, menteeId);

        return meetingRepository.findByMentorIdAndMenteeIdOrderByScheduledAtAsc(mentor.getId(), mentee.getId())
                .stream()
                .map(meeting -> new MentorDtos.MeetingResponse(
                        meeting.getId(),
                        mentor.getId(),
                        mentee.getId(),
                        meeting.getScheduledAt(),
                        meeting.getAgenda(),
                        meeting.getMeetingLink(),
                        meeting.getCreatedAt()
                ))
                .toList();
    }

    private User validateAssignedMentee(User mentor, Long menteeId) {
        if (mentor.getRole() != Role.MENTOR) {
            throw new BadRequestException("Current user is not mentor");
        }

        User mentee = userRepository.findById(menteeId)
                .orElseThrow(() -> new NotFoundException("Mentee not found"));

        if (mentee.getRole() != Role.MENTEE) {
            throw new BadRequestException("Provided user is not a mentee");
        }

        assignmentRepository.findByMentorIdAndMenteeId(mentor.getId(), menteeId)
                .orElseThrow(() -> new BadRequestException("This mentee is not assigned to current mentor"));

        return mentee;
    }

    private MentorDtos.TaskResponse toTaskResponse(TaskItem task, TaskReview menteeReview, TaskReview mentorReview) {
        return new MentorDtos.TaskResponse(
                task.getId(),
                task.getMentor().getId(),
                task.getMentee().getId(),
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
