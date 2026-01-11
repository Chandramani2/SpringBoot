package com.membership.program.Config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter requestCounter(MeterRegistry registry) {
        return Counter.builder("http.requests.total")  // metric name
                .description("Total number of HTTP requests")  // metric description
                .tags("app", "membership-program")  // metric labels
                .register(registry);  // register with Spring's metric registry
    }

    @Bean
    public AtomicInteger gaugeActive(MeterRegistry registry) {
        return registry.gauge("http.requests.active",
                Tags.of("app", "membership-program"),
                new AtomicInteger(0));
    }

    @Bean
    public Timer requestLatencyHistogram(MeterRegistry registry) {
        return Timer.builder("http.request.duration.seconds")
                .description("HTTP request duration in seconds")
                .tags("app", "membership-program")
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(registry);
    }
}
