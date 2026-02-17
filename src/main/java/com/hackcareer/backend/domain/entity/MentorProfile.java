package com.hackcareer.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mentor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "mentor_id", nullable = false, unique = true)
    private User mentor;

    @Column(length = 1200)
    private String about;

    @Column(length = 1200)
    private String review;

    private Double rating;
}
