package com.membership.program.Models;


import com.membership.program.Constants.MembershipStatus;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private MembershipStatus status = MembershipStatus.INACTIVE;

    // Foreign Key to the pre-defined master table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription currentPlan;

    private LocalDateTime startDate; // When the user started THIS plan
    private LocalDateTime expiryDate; // When this specific user's plan expires

    @Version
    private Integer version; // For concurrency best practices
}