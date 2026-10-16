package io.kairos.domain;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public record JobOfferSource(
        UUID id,
        UUID jobOfferId,
        URI url,
        String platform,
        String externalId,
        Instant collectedAt
) {
    public JobOfferSource {
        Checks.required(id, "id");
        Checks.required(jobOfferId, "jobOfferId");
        Checks.required(url, "url");
        Checks.required(collectedAt, "collectedAt");
    }
}

