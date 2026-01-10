package com.membership.program.Service;

import com.membership.program.Constants.MembershipStatus;
import com.membership.program.Constants.PlanType;
import com.membership.program.Constants.TierName;
import com.membership.program.Models.MembershipTier;
import com.membership.program.Models.Subscription;
import com.membership.program.Models.User;
import com.membership.program.Repository.SubscriptionRepository;
import com.membership.program.Repository.TierRepository;
import com.membership.program.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {

    private final UserRepository userRepository;
    private final TierRepository tierRepository;
    private final SubscriptionRepository subscriptionRepository;

    private static final int MAX_RETRIES = 3;

    // 1. Get All Configurable Tiers
    public List<MembershipTier> getAllTiers() {
        return tierRepository.findAll();
    }

    // 2. Subscribe to a pre-defined plan matrix entry
    @Transactional
    public User subscribe(Long userId, TierName tierName, PlanType planType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Lookup the unique combination (Fixes the "2 results returned" error)
        Subscription targetPlan = subscriptionRepository
                .findByTier_TierNameAndPlanType(tierName, planType)
                .orElseThrow(() -> new RuntimeException("Plan combination not found"));

        user.setCurrentPlan(targetPlan);
        user.setStatus(planType == PlanType.NONE ? MembershipStatus.INACTIVE : MembershipStatus.ACTIVE);

        // Logical update: Dates are set based on current execution time
        user.setStartDate(LocalDateTime.now());
        user.setExpiryDate(calculateExpiry(planType));

        return userRepository.save(user);
    }

    private LocalDateTime calculateExpiry(PlanType planType) {
        LocalDateTime now = LocalDateTime.now();
        return switch (planType) {
            case MONTHLY -> now.plusMonths(1);
            case QUARTERLY -> now.plusMonths(3);
            case YEARLY -> now.plusYears(1);
            case NONE -> LocalDateTime.now().plusYears(100); // Represents 'Infinite'/Permanent Base
        };
    }

    // 3. Upgrade/Downgrade by switching the plan reference
    @Transactional
    public User changeTier(Long userId, TierName newTierName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != MembershipStatus.ACTIVE) {
            throw new RuntimeException("Cannot change tier: Subscription is not active");
        }

        // Keep existing PlanType but change the Tier
        PlanType currentPlanType = user.getCurrentPlan().getPlanType();

        Subscription newPlan = (Subscription) subscriptionRepository
                .findByTier_TierNameAndPlanType(newTierName, currentPlanType)
                .orElseThrow(() -> new RuntimeException("New tier combination not found in catalog"));

        user.setCurrentPlan(newPlan);
        return userRepository.save(user);
    }

    // 4. Cancel Subscription (Resets to NONE-NONE plan)
    @Transactional
    public User cancelSubscription(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        Subscription nonePlan = (Subscription) subscriptionRepository
                .findByTier_TierNameAndPlanType(TierName.NONE, PlanType.NONE)
                .orElseThrow(() -> new RuntimeException("Default NONE plan not found"));

        user.setCurrentPlan(nonePlan);
        user.setStatus(MembershipStatus.INACTIVE);
        user.setExpiryDate(LocalDateTime.now()); // Set expiry to now

        return userRepository.save(user);
    }


    public Subscription getUserSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getCurrentPlan();
    }

    /**
     * Requirement 4: Concurrent Updates with @Version Check
     */
    public void simulateConcurrentUpdate(Long userId) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Simulation: Two threads trying to change the user's plan at the same time
        executor.execute(() -> updateTierWithRetry(userId, TierName.GOLD));
        executor.execute(() -> updateTierWithRetry(userId, TierName.PLATINUM));

        executor.shutdown();
    }

    @Transactional
    public void updateTierWithRetry(Long userId, TierName newTierName) {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                // FIX: Use the join-fetch method here
                User user = userRepository.findByIdWithPlan(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                // This will now work because currentPlan is already loaded
                PlanType currentType = user.getCurrentPlan().getPlanType();

                if (currentType == PlanType.NONE) {
                    currentType = PlanType.MONTHLY;
                }

                Subscription newPlan = subscriptionRepository
                        .findByTier_TierNameAndPlanType(newTierName, currentType)
                        .orElseThrow(() -> new RuntimeException("Plan combination not found"));

                user.setCurrentPlan(newPlan);
                user.setStatus(MembershipStatus.ACTIVE);

                // Version check happens at flush
                userRepository.saveAndFlush(user);

                log.info("Successfully updated User {} to {} on attempt {}", userId, newTierName, attempts + 1);
                return;

            } catch (ObjectOptimisticLockingFailureException e) {
                attempts++;
                log.warn("Conflict detected for user {}. Retry {}/{}", userId, attempts, MAX_RETRIES);
                if (attempts >= MAX_RETRIES) {
                    throw new RuntimeException("Max retries reached.");
                }
                try { Thread.sleep(100); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
    }
}