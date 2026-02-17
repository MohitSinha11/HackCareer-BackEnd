package com.hackcareer.backend.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mentee_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenteeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "mentee_id", nullable = false, unique = true)
    private User mentee;

    @Column(length = 1200)
    private String bio;
}
