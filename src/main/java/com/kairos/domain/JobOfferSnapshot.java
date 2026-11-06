package com.kairos.domain;

import java.time.Instant;
import java.util.UUID;

/** Immutable capture of a job offer at a point in time. */
public record JobOfferSnapshot(
        UUID id,
        UUID jobOfferId,
        String title,
        String companyName,
        String content,
        String location,
        String country,
        String workArrangement,
        Instant capturedAt
) {
    public JobOfferSnapshot {
        Checks.required(id, "id");
        Checks.required(jobOfferId, "jobOfferId");
        Checks.text(title, "title");
        Checks.text(companyName, "companyName");
        Checks.text(content, "content");
        Checks.required(capturedAt, "capturedAt");
    }
}
