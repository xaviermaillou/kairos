package io.kairos.domain;

import java.time.Instant;
import java.util.UUID;

public record Application(
        UUID id,
        UUID userId,
        UUID jobOfferId,
        Instant sentAt,
        String channel,
        ApplicationStatus status,
        JobOfferSnapshot offerSnapshot
) {
    public Application {
        Checks.required(id, "id");
        Checks.required(userId, "userId");
        Checks.required(jobOfferId, "jobOfferId");
        Checks.required(sentAt, "sentAt");
        Checks.required(status, "status");
        Checks.required(offerSnapshot, "offerSnapshot");
    }
}

