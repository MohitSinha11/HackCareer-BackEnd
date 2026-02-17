package com.hackcareer.backend.mentee;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mentee")
@RequiredArgsConstructor
public class MenteeController {

    private final MenteeService menteeService;

    @GetMapping("/profile")
    public MenteeDtos.MenteeProfileResponse getMyProfile() {
        return menteeService.getMyProfile();
    }

    @GetMapping("/tasks")
    public List<MenteeDtos.MenteeTaskResponse> getMyTasks() {
        return menteeService.getMyTasks();
    }

    @PostMapping("/tasks/{taskId}/complete")
    public MenteeDtos.MenteeTaskResponse completeTask(
            @PathVariable Long taskId,
            @Valid @RequestBody MenteeDtos.CompleteTaskRequest request
    ) {
        return menteeService.completeTask(taskId, request);
    }

    @GetMapping("/meetings")
    public List<MenteeDtos.MenteeMeetingResponse> getMyMeetings() {
        return menteeService.getMyMeetings();
    }
}
