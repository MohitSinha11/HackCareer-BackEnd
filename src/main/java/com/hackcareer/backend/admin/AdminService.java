package com.hackcareer.backend.admin;

import com.hackcareer.backend.common.BadRequestException;
import com.hackcareer.backend.common.NotFoundException;
import com.hackcareer.backend.domain.entity.MenteeProfile;
import com.hackcareer.backend.domain.entity.MentorMenteeAssignment;
import com.hackcareer.backend.domain.entity.MentorProfile;
import com.hackcareer.backend.domain.entity.User;
import com.hackcareer.backend.domain.enums.Role;
import com.hackcareer.backend.domain.repository.MenteeProfileRepository;
import com.hackcareer.backend.domain.repository.MentorMenteeAssignmentRepository;
import com.hackcareer.backend.domain.repository.MentorProfileRepository;
import com.hackcareer.backend.domain.repository.UserRepository;
import com.hackcareer.backend.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;
    private final MentorMenteeAssignmentRepository assignmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    @Transactional
    public AdminDtos.UserSummary createMentor(AdminDtos.CreateMentorRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        User mentor = userRepository.save(User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.MENTOR)
                .enabled(true)
                .build());

        mentorProfileRepository.save(MentorProfile.builder()
                .mentor(mentor)
                .about(request.about())
                .review(request.review())
                .rating(request.rating())
                .build());

        return toSummary(mentor);
    }

    @Transactional
    public AdminDtos.UserSummary createMentee(AdminDtos.CreateMenteeRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists");
        }

        User mentee = userRepository.save(User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.MENTEE)
                .enabled(true)
                .build());

        menteeProfileRepository.save(MenteeProfile.builder()
                .mentee(mentee)
                .bio(request.bio())
                .build());

        return toSummary(mentee);
    }

    @Transactional
    public AdminDtos.AssignmentResponse assignMentor(AdminDtos.AssignMentorRequest request) {
        User admin = currentUserService.currentUser();

        User mentor = userRepository.findById(request.mentorId())
                .orElseThrow(() -> new NotFoundException("Mentor not found"));
        User mentee = userRepository.findById(request.menteeId())
                .orElseThrow(() -> new NotFoundException("Mentee not found"));

        if (mentor.getRole() != Role.MENTOR) {
            throw new BadRequestException("Provided mentorId does not belong to mentor");
        }
        if (mentee.getRole() != Role.MENTEE) {
            throw new BadRequestException("Provided menteeId does not belong to mentee");
        }

        if (assignmentRepository.existsByMentorIdAndMenteeId(mentor.getId(), mentee.getId())) {
            throw new BadRequestException("Mentor already assigned to this mentee");
        }

        MentorMenteeAssignment assignment = assignmentRepository.save(MentorMenteeAssignment.builder()
                .mentor(mentor)
                .mentee(mentee)
                .admin(admin)
                .build());

        return new AdminDtos.AssignmentResponse(
                assignment.getId(),
                mentor.getId(),
                mentee.getId(),
                admin.getId(),
                assignment.getAssignedAt().toString()
        );
    }

    public List<AdminDtos.UserSummary> getAllMentors() {
        return userRepository.findByRole(Role.MENTOR).stream().map(this::toSummary).toList();
    }

    public List<AdminDtos.UserSummary> getAllMentees() {
        return userRepository.findByRole(Role.MENTEE).stream().map(this::toSummary).toList();
    }

    public List<AdminDtos.UserSummary> getAllUsers() {
        return userRepository.findAll().stream().map(this::toSummary).toList();
    }

    public AdminDtos.AdminProfileResponse getMyProfile() {
        User admin = currentUserService.currentUser();
        return new AdminDtos.AdminProfileResponse(
                admin.getId(),
                admin.getFullName(),
                admin.getEmail(),
                admin.getRole().name()
        );
    }

    private AdminDtos.UserSummary toSummary(User user) {
        return new AdminDtos.UserSummary(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
