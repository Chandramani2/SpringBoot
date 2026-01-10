package com.membership.program.Controller;

import com.membership.program.Constants.PlanType;
import com.membership.program.Constants.TierName;
import com.membership.program.Models.MembershipTier;
import com.membership.program.Models.Subscription;
import com.membership.program.Models.User;
import com.membership.program.Service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membership")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @GetMapping("/tiers")
    public ResponseEntity<List<MembershipTier>> getTiers() {
        return ResponseEntity.ok(membershipService.getAllTiers());
    }

    // test for system-cpu-usage metrics prometheus
//    @GetMapping("/tiers")
//    public ResponseEntity<List<MembershipTier>> getTiers() {
//
//        try {
//            boolean condition = true;
//            while (condition) {
//                Runnable r = () -> {
//                    while (true) {
//                    }
//                };
//                new Thread(r).start();
//                Thread.sleep(5000);
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return ResponseEntity.ok(membershipService.getAllTiers());
//    }

    @PostMapping("/subscribe")
    public ResponseEntity<User> subscribe(@RequestParam Long userId,
                                          @RequestParam TierName tier,
                                          @RequestParam PlanType plan) {
        return ResponseEntity.ok(membershipService.subscribe(userId, tier, plan));
    }

    @PutMapping("/change-tier")
    public ResponseEntity<User> changeTier(@RequestParam Long userId,
                                           @RequestParam TierName newTier) {
        return ResponseEntity.ok(membershipService.changeTier(userId, newTier));
    }

    @DeleteMapping("/cancel/{userId}")
    public ResponseEntity<User> cancel(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.cancelSubscription(userId));
    }

    @GetMapping("/track/{userId}")
    public ResponseEntity<Subscription> track(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getUserSubscription(userId));
    }
}