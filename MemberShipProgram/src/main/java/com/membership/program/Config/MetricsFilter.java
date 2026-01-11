package com.membership.program.Config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MetricsFilter extends OncePerRequestFilter {
    private final Counter requestCounter;
    private final AtomicInteger activeRequests;
    private final Timer requestLatencyHistogram;

    public MetricsFilter(Counter requestCounter,
                         AtomicInteger activeRequests,
                         Timer requestLatencyHistogram) {
        this.requestCounter = requestCounter;
        this.activeRequests = activeRequests;
        this.requestLatencyHistogram = requestLatencyHistogram;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        activeRequests.incrementAndGet();

        Timer.Sample sample = Timer.start();
        try {
            requestCounter.increment();
            filterChain.doFilter(request, response);
        } finally {
            sample.stop(requestLatencyHistogram);
            activeRequests.decrementAndGet();
        }
    }
}
