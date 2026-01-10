package com.membership.program.Models;

import com.membership.program.Constants.PlanType;
import lombok.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Requirement: Must link to a Tier (SILVER, GOLD, PLATINUM, NONE)
    @ManyToOne(optional = false) // Ensures tier_id cannot be null
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType planType = PlanType.NONE;

    private Double price; // Price specific to this combination

}