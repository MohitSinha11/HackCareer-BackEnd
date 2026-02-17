package com.hackcareer.backend.mentor;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MentorDtos {

    public record MenteeSummary(
            Long id,
            String fullName,
            String email
    ) {
    }

    public record CreateTaskRequest(
            @NotNull Long menteeId,
            @NotBlank String title,
            String description,
            LocalDate dueDate
    ) {
    }

    public record TaskResponse(
            Long id,
            Long mentorId,
            Long menteeId,
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

    public record ReviewTaskRequest(
            @NotNull @Min(1) @Max(5) Integer rating,
            @NotBlank String comment
    ) {
    }

    public record CreateMeetingRequest(
            @NotNull Long menteeId,
            @NotNull @Future LocalDateTime scheduledAt,
            String agenda,
            String meetingLink
    ) {
    }

    public record MeetingResponse(
            Long id,
            Long mentorId,
            Long menteeId,
            LocalDateTime scheduledAt,
            String agenda,
            String meetingLink,
            LocalDateTime createdAt
    ) {
    }

    public record MentorProfileResponse(
            Long mentorId,
            String fullName,
            String email,
            String about,
            String review,
            Double rating
    ) {
    }
}
