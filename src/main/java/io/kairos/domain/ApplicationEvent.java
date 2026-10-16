package io.kairos.domain;

import java.time.Instant;
import java.util.UUID;

/** Audit entry for an applied change. A null suggestionId identifies a manual action. */
public record ApplicationEvent(
        UUID id,
        UUID applicationId,
        UUID actorUserId,
        ApplicationChange change,
        Instant occurredAt,
        UUID suggestionId
) {
    public ApplicationEvent {
        Checks.required(id, "id");
        Checks.required(applicationId, "applicationId");
        Checks.required(actorUserId, "actorUserId");
        Checks.required(change, "change");
        Checks.required(occurredAt, "occurredAt");
        Checks.belongsTo(change, applicationId);
    }
}

