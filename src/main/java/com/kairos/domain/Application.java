package com.kairos.domain;

import java.time.Instant;
import java.util.UUID;

public record Application(
        UUID id,
        UUID userId,
        Instant sentAt,
        String channel,
        ApplicationStatus status,
        JobOfferSnapshot offerSnapshot
) {
    public Application {
        Checks.required(id, "id");
        Checks.required(userId, "userId");
        Checks.required(sentAt, "sentAt");
        Checks.required(status, "status");
        Checks.required(offerSnapshot, "offerSnapshot");
    }

    public Application changeStatus(ApplicationStatus next) {
        Checks.required(next, "next");

        if (status == next) {
            throw new IllegalArgumentException("Application already has status " + status);
        }

        boolean transitionAllowed = switch (status) {
            case SENT -> next == ApplicationStatus.INTERVIEWING
                    || next == ApplicationStatus.CLOSED;
            case INTERVIEWING -> next == ApplicationStatus.CLOSED;
            case CLOSED -> false;
        };

        if (!transitionAllowed) {
            throw new IllegalStateException("Cannot change status from " + status + " to " + next);
        }

        return new Application(
                id,
                userId,
                sentAt,
                channel,
                next,
                offerSnapshot
        );
    }
}
