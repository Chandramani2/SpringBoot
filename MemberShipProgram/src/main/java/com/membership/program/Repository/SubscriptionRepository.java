package com.membership.program.Repository;

import com.membership.program.Constants.PlanType;
import com.membership.program.Constants.TierName;
import com.membership.program.Models.Subscription;
import com.membership.program.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Custom query to find subscriptions by a specific tier
    @Query("SELECT s FROM Subscription s WHERE s.tier.tierName = :tierName")
    List<Subscription> findAllByTierName(@Param("tierName") TierName tierName);

    // Explicitly find the unique combination of Tier Name and Plan Type
    Optional<Subscription> findByTier_TierNameAndPlanType(TierName tierName, PlanType planType);

}