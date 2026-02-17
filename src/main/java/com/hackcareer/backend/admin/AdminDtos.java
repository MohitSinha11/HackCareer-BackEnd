package com.hackcareer.backend.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AdminDtos {

    public record CreateMentorRequest(
            @NotBlank String fullName,
            @Email @NotBlank String email,
            @NotBlank String password,
            String about,
            String review,
            Double rating
    ) {
    }

    public record CreateMenteeRequest(
            @NotBlank String fullName,
            @Email @NotBlank String email,
            @NotBlank String password,
            String bio
    ) {
    }

    public record AssignMentorRequest(
            @NotNull Long mentorId,
            @NotNull Long menteeId
    ) {
    }

    public record UserSummary(
            Long id,
            String fullName,
            String email,
            String role
    ) {
    }

    public record AdminProfileResponse(
            Long id,
            String fullName,
            String email,
            String role
    ) {
    }

    public record AssignmentResponse(
            Long id,
            Long mentorId,
            Long menteeId,
            Long adminId,
            String assignedAt
    ) {
    }
}
