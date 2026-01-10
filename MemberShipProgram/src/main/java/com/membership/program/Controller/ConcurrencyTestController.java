package com.membership.program.Controller;

import com.membership.program.Service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/membership/test")
@RequiredArgsConstructor
public class ConcurrencyTestController {

    private final MembershipService membershipService;

    @PostMapping("/concurrency/{userId}")
    public String testLocking(@PathVariable Long userId) {
        membershipService.simulateConcurrentUpdate(userId);
        return "Concurrency test triggered. Check server logs for lock results.";
    }
}