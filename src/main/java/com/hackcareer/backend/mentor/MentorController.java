package com.hackcareer.backend.mentor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mentor")
@RequiredArgsConstructor
public class MentorController {

    private final MentorService mentorService;

    @GetMapping("/mentees")
    public List<MentorDtos.MenteeSummary> getAssignedMentees() {
        return mentorService.getAssignedMentees();
    }

    @PostMapping("/tasks")
    public MentorDtos.TaskResponse createTask(@Valid @RequestBody MentorDtos.CreateTaskRequest request) {
        return mentorService.createTask(request);
    }

    @PostMapping("/tasks/{taskId}/review")
    public MentorDtos.TaskResponse reviewTaskForMentee(
            @PathVariable Long taskId,
            @Valid @RequestBody MentorDtos.ReviewTaskRequest request
    ) {
        return mentorService.reviewTaskForMentee(taskId, request);
    }

    @PostMapping("/meetings")
    public MentorDtos.MeetingResponse createMeeting(@Valid @RequestBody MentorDtos.CreateMeetingRequest request) {
        return mentorService.createMeeting(request);
    }

    @GetMapping("/tasks/{menteeId}")
    public List<MentorDtos.TaskResponse> getTasksByMentee(@PathVariable Long menteeId) {
        return mentorService.getTasksByMentee(menteeId);
    }

    @GetMapping("/meetings/{menteeId}")
    public List<MentorDtos.MeetingResponse> getMeetingsByMentee(@PathVariable Long menteeId) {
        return mentorService.getMeetingsByMentee(menteeId);
    }

    @GetMapping("/profile")
    public MentorDtos.MentorProfileResponse getProfile() {
        return mentorService.getMyProfile();
    }
}
