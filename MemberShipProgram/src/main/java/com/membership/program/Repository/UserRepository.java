package com.membership.program.Repository;

import com.membership.program.Constants.MembershipStatus;
import com.membership.program.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Find users by their active membership status
    List<User> findByStatus(MembershipStatus status);

    // Find a user by email for authentication or profile tracking
    Optional<User> findByEmail(String email);

    // This query fetches the plan and tier immediately, avoiding the Proxy error
    @Query("SELECT u FROM User u " +
            "JOIN FETCH u.currentPlan p " +
            "JOIN FETCH p.tier " +
            "WHERE u.id = :id")
    Optional<User> findByIdWithPlan(@Param("id") Long id);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}