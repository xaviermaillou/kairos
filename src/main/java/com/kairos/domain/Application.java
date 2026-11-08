package com.kairos.domain;

import com.kairos.validation.Checks;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record Application(
        UUID id,
        UUID userId,
        Instant sentAt,
        String channel,
        ApplicationStatus status,
        ApplicationResult result,
        JobOfferSnapshot offerSnapshot
) {
    public enum ApplicationStatus {
        SENT,
        ONGOING,
        ARCHIVED
    }

    public enum ApplicationResult {
        SUCCEEDED,
        REJECTED,
        WITHDRAWN,
        NO_RESPONSE
    }

    public Application {
        Checks.required(id, "id");
        Checks.required(userId, "userId");
        Checks.required(sentAt, "sentAt");
        Checks.required(status, "status");
        Checks.required(offerSnapshot, "offerSnapshot");
    }

    public Application changeStatus(ApplicationStatus nextStatus) {
        Checks.required(nextStatus, "nextStatus");

        if (status == nextStatus) {
            throw new IllegalStateException("Application already has status " + status);
        }

        return new Application(
                id,
                userId,
                sentAt,
                channel,
                nextStatus,
                result,
                offerSnapshot
        );
    }

    public Application changeResult(ApplicationResult nextResult) {
        if (Objects.equals(result, nextResult)) {
            throw new IllegalStateException("Application already has result " + result);
        }

        return new Application(
                id,
                userId,
                sentAt,
                channel,
                status,
                nextResult,
                offerSnapshot
        );
    }
}
