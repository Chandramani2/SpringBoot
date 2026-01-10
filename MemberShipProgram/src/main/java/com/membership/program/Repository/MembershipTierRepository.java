package com.membership.program.Repository;

import com.membership.program.Constants.TierName;
import com.membership.program.Models.MembershipTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MembershipTierRepository extends JpaRepository<MembershipTier, Long> {
    // Fetch tier details by the Enum name
    Optional<MembershipTier> findByTierName(TierName tierName);
}