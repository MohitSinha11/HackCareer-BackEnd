package com.hackcareer.backend.mentee;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MenteeDtos {

    public record MenteeProfileResponse(
            Long id,
            String fullName,
            String email,
            String bio
    ) {
    }

    public record MenteeTaskResponse(
            Long id,
            Long mentorId,
            String mentorName,
            String title,
            String description,
            LocalDate dueDate,
            String status,
            LocalDateTime completedAt,
            String menteeReviewForMentor,
            Integer menteeRatingForMentor,
            String mentorReviewForMentee,
            Integer mentorRatingForMentee,
            LocalDateTime createdAt
    ) {
    }

    public record CompleteTaskRequest(
            @NotNull @Min(1) @Max(5) Integer rating,
            @NotBlank String comment
    ) {
    }

    public record MenteeMeetingResponse(
            Long id,
            Long mentorId,
            String mentorName,
            LocalDateTime scheduledAt,
            String agenda,
            String meetingLink,
            LocalDateTime createdAt
    ) {
    }
}
