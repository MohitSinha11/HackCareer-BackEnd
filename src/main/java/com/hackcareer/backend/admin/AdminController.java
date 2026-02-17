package com.hackcareer.backend.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/users/mentor")
    public AdminDtos.UserSummary createMentor(@Valid @RequestBody AdminDtos.CreateMentorRequest request) {
        return adminService.createMentor(request);
    }

    @PostMapping("/users/mentee")
    public AdminDtos.UserSummary createMentee(@Valid @RequestBody AdminDtos.CreateMenteeRequest request) {
        return adminService.createMentee(request);
    }

    @PostMapping("/assignments")
    public AdminDtos.AssignmentResponse assignMentor(@Valid @RequestBody AdminDtos.AssignMentorRequest request) {
        return adminService.assignMentor(request);
    }

    @GetMapping("/mentors")
    public List<AdminDtos.UserSummary> getMentors() {
        return adminService.getAllMentors();
    }

    @GetMapping("/mentees")
    public List<AdminDtos.UserSummary> getMentees() {
        return adminService.getAllMentees();
    }

    @GetMapping("/users")
    public List<AdminDtos.UserSummary> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/profile")
    public AdminDtos.AdminProfileResponse getProfile() {
        return adminService.getMyProfile();
    }
}
