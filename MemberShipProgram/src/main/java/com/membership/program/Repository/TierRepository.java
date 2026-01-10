package com.membership.program.Repository;

import com.membership.program.Constants.TierName;
import com.membership.program.Models.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for managing Membership Tiers (Silver, Gold, Platinum)
 * This supports the requirement for configurable perks per tier
 */
@Repository
public interface TierRepository extends JpaRepository<MembershipTier, Long> {

    /**
     * Finds a tier by its Enum name.
     * Used during subscription or tier movement (upgrade/downgrade)
     *
     * @param tierName The Enum value (SILVER, GOLD, PLATINUM)
     * @return Optional containing the tier configuration
     */
    Optional<MembershipTier> findByTierName(TierName tierName);
}