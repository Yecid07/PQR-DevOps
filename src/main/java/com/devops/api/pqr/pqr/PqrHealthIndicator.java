package com.devops.api.pqr.pqr;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PqrHealthIndicator implements HealthIndicator {

    @Value("${app.version:1.0.0}")
    private String appVersion;

    @Value("${app.deployment.type:stable}")
    private String deploymentType;

    @Override
    public Health health() {
        return Health.up()
                .withDetail("status", deploymentType)
                .withDetail("version", appVersion)
                .withDetail("application", "pqr-api")
                .withDetail("checkedAt", LocalDateTime.now().toString())
                .build();
    }
}