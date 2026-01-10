package com.membership.program.Models;

import com.membership.program.Constants.TierName;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
public class MembershipTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true)
    private TierName tierName;

    // Configurable Benefits
    private Double discountPercentage; // Extra X% discount
    private Boolean freeDelivery; // Free delivery on eligible orders
    private Boolean earlyAccess; // Early access to sales/exclusive deals
    private Boolean prioritySupport; // Priority support for premium members
    private String exclusiveCoupons; // Additional perks like exclusive coupons
}