package io.kairos.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** A proposed update produced from an email analysis; it is never applied silently. */
public record UpdateSuggestion(
        UUID id,
        UUID userId,
        UUID sourceEmailId,
        UUID applicationId,
        List<ApplicationChange> changes,
        String justification,
        Instant createdAt,
        SuggestionStatus status,
        Instant decidedAt
) {
    public UpdateSuggestion {
        Checks.required(id, "id");
        Checks.required(userId, "userId");
        Checks.required(sourceEmailId, "sourceEmailId");
        Checks.required(applicationId, "applicationId");
        Checks.text(justification, "justification");
        Checks.required(createdAt, "createdAt");
        Checks.required(status, "status");
        changes = changes == null ? List.of() : List.copyOf(changes);
        if (changes.isEmpty()) {
            throw new IllegalArgumentException("A suggestion requires at least one change");
        }
        changes.forEach(change -> Checks.belongsTo(change, applicationId));
        if ((status == SuggestionStatus.PENDING) != (decidedAt == null)) {
            throw new IllegalArgumentException("Only decided suggestions require a decision date");
        }
        if (decidedAt != null && decidedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Decision date cannot precede creation date");
        }
    }

    public UpdateSuggestion decide(SuggestionStatus decision, Instant at) {
        if (status != SuggestionStatus.PENDING) {
            throw new IllegalStateException("Suggestion has already been decided");
        }
        if (decision == SuggestionStatus.PENDING) {
            throw new IllegalArgumentException("A final decision is required");
        }
        return new UpdateSuggestion(
                id, userId, sourceEmailId, applicationId, changes,
                justification, createdAt, decision, Checks.required(at, "at")
        );
    }
}

