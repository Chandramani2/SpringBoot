package com.membership.program.Controller;


import com.membership.program.Constants.*;
import com.membership.program.Models.*;
import com.membership.program.Repository.SubscriptionRepository;
import com.membership.program.Repository.TierRepository;
import com.membership.program.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/admin/data")
@RequiredArgsConstructor
public class DataPopulationController {

    private final TierRepository tierRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    @PostMapping("/setup-complete-data")
    public ResponseEntity<Map<String, Object>> populateSystem(@RequestBody List<User> userList) {
        // 1. Initialize/Update Configurable Membership Tiers [cite: 12]
        List<String> tierNames = Arrays.asList("NONE", "SILVER", "GOLD", "PLATINUM");
        Map<TierName, MembershipTier> tiers = initializeTiers();

        // 2. Initialize/Update Subscription Plan Matrix (Combination of Tier + Plan Type) [cite: 6, 18]
        List<String> matrixGenerated = initializeSubscriptionMatrix(tiers);

        // 3. Get the Default "NONE" Plan for initial assignment
        Subscription nonePlan = (Subscription) subscriptionRepository
                .findByTier_TierNameAndPlanType(TierName.NONE, PlanType.NONE)
                .orElseThrow(() -> new RuntimeException("Master Data not initialized"));

        // 4. Upsert Users (Update if exists by email, else Insert) [cite: 22]
        int userCount = 0;
        for (User userReq : userList) {
            User user = userRepository.findByEmail(userReq.getEmail())
                    .orElse(userReq);

            user.setName(userReq.getName());
            user.setPhoneNumber(userReq.getPhoneNumber());
            user.setStatus(MembershipStatus.INACTIVE); // Status remains inactive until subscription [cite: 15]
            user.setCurrentPlan(nonePlan); // Default assignment to NONE plan [cite: 14]
            user.setStartDate(LocalDateTime.now());
            user.setExpiryDate(LocalDateTime.now()); // Default base duration

            userRepository.save(user);
            userCount++;
        }

        // Prepare the specific JSON response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Initialized " + tierNames.size() + " tiers, " + matrixGenerated.size() + " plans, and " + userCount + " users.");
        response.put("status", "SUCCESS");

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("tiersCreated", tierNames);
        details.put("planMatrixGenerated", matrixGenerated);
        details.put("usersLinkedToDefaultPlan", userCount);

        response.put("details", details);

        return ResponseEntity.ok(response);
    }

    private Map<TierName, MembershipTier> initializeTiers() {
        Map<TierName, MembershipTier> map = new HashMap<>();
        // Define perks for each tier [cite: 12]
        saveOrUpdateTier(map, TierName.NONE, 0.0, false, false, false);
        saveOrUpdateTier(map, TierName.SILVER, 5.0, true, false, false); // 5% Discount [cite: 9]
        saveOrUpdateTier(map, TierName.GOLD, 10.0, true, true, true); // 10% Discount + Priority [cite: 9, 11]
        saveOrUpdateTier(map, TierName.PLATINUM, 20.0, true, true, true); // 20% Discount [cite: 9, 12]
        return map;
    }

    private void saveOrUpdateTier(Map<TierName, MembershipTier> map, TierName name, Double discount, boolean delivery, boolean access, boolean support) {
        MembershipTier t = tierRepository.findByTierName(name).orElse(new MembershipTier());
        t.setTierName(name);
        t.setDiscountPercentage(discount); // [cite: 9]
        t.setFreeDelivery(delivery); // Free delivery on eligible orders [cite: 8]
        t.setEarlyAccess(access); // Early access to sales [cite: 10]
        t.setPrioritySupport(support); // Priority support for premium members [cite: 11]
        map.put(name, tierRepository.save(t));
    }

    private List<String> initializeSubscriptionMatrix(Map<TierName, MembershipTier> tiers) {
        List<String> matrixNames = new ArrayList<>();
        for (MembershipTier tier : tiers.values()) {
            for (PlanType plan : PlanType.values()) {
                // Rule: NONE tier only pairs with NONE plan, others pair with valid billing cycles [cite: 6, 18]
                if ((tier.getTierName() == TierName.NONE) == (plan == PlanType.NONE)) {
                    Subscription sub = (Subscription) subscriptionRepository.findByTier_TierNameAndPlanType(tier.getTierName(), plan)
                            .orElse(new Subscription());

                    sub.setTier(tier);
                    sub.setPlanType(plan);
                    sub.setPrice(calculateBasePrice(tier.getTierName(), plan)); // Configurable pricing [cite: 6]
                    subscriptionRepository.save(sub);

                    matrixNames.add(tier.getTierName() + "-" + plan.name());
                }
            }
        }
        return matrixNames;
    }

    private Double calculateBasePrice(TierName tier, PlanType plan) {
        if (plan == PlanType.NONE) return 0.0;
        double base = (tier == TierName.GOLD) ? 500.0 : (tier == TierName.PLATINUM) ? 1000.0 : 200.0;
        return switch (plan) {
            case MONTHLY -> base; // [cite: 6]
            case QUARTERLY -> base * 2.5; // [cite: 6]
            case YEARLY -> base * 9.0; // [cite: 6]
            default -> 0.0;
        };
    }
}