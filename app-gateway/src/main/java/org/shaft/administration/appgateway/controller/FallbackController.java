package org.shaft.administration.appgateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback1")
    public String userFallback() {
        return "User services is not available";
    }

    @GetMapping("/auth-fallback")
    public String authFallback() {
        return "Auth services is not available";
    }

    @GetMapping("/circuit-breaker-fallback")
    public String circuitBreakerFallback() {
        return "An error occurred. Please try after some time or contact support team";
    }
}
