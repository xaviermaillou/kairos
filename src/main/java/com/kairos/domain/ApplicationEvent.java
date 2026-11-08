package com.kairos.domain;

import com.kairos.domain.Application.ApplicationResult;
import com.kairos.domain.Application.ApplicationStatus;
import com.kairos.validation.Checks;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** A proposed, applied or rejected event concerning an application. */
public record ApplicationEvent(
        UUID id,
        UUID applicationId,
        UUID actorUserId,
        EventDetails details,
        EventStatus status,
        Instant createdAt,
        Instant decidedAt
) {
    public enum EventStatus {
        PENDING,
        APPLIED,
        REJECTED
    }

    public sealed interface EventDetails {}

    public record StatusChanged(
            ApplicationStatus previous,
            ApplicationStatus next
    ) implements EventDetails {
        public StatusChanged {
            Checks.required(previous, "previous");
            Checks.required(next, "next");
            if (previous == next) {
                throw new IllegalArgumentException("Status must change");
            }
        }
    }

    public record ResultChanged(
            ApplicationResult previous,
            ApplicationResult next
    ) implements EventDetails {
        public ResultChanged {
            if (Objects.equals(previous, next)) {
                throw new IllegalArgumentException("Result must change");
            }
        }
    }

    public record StepAdded(ApplicationStep step) implements EventDetails {
        public StepAdded {
            Checks.required(step, "step");
        }
    }

    public record StepRemoved(ApplicationStep step) implements EventDetails {
        public StepRemoved {
            Checks.required(step, "step");
        }
    }

    public record StepChanged(
            ApplicationStep previous,
            ApplicationStep next
    ) implements EventDetails {
        public StepChanged {
            Checks.required(previous, "previous");
            Checks.required(next, "next");
            if (!previous.id().equals(next.id())
                    || !previous.applicationId().equals(next.applicationId())) {
                throw new IllegalArgumentException("Application step identity cannot change");
            }
            if (previous.equals(next)) {
                throw new IllegalArgumentException("Application step must change");
            }
        }
    }

    public ApplicationEvent {
        Checks.required(id, "id");
        Checks.required(applicationId, "applicationId");
        Checks.required(actorUserId, "actorUserId");
        Checks.required(details, "details");
        Checks.required(status, "status");
        Checks.required(createdAt, "createdAt");
        ensureBelongsToApplication(details, applicationId);

        if (status == EventStatus.PENDING && decidedAt != null) {
            throw new IllegalArgumentException("A pending event cannot have a decision date");
        }
        if (status != EventStatus.PENDING && decidedAt == null) {
            throw new IllegalArgumentException("A decided event requires a decision date");
        }
        if (decidedAt != null && decidedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("Decision date cannot precede creation date");
        }
    }

    public static ApplicationEvent applied(
            UUID applicationId,
            UUID actorUserId,
            EventDetails details,
            Instant at
    ) {
        return new ApplicationEvent(
                UUID.randomUUID(),
                applicationId,
                actorUserId,
                details,
                EventStatus.APPLIED,
                at,
                at
        );
    }

    public ApplicationEvent decide(EventStatus decision, Instant at) {
        if (status != EventStatus.PENDING) {
            throw new IllegalStateException("Event has already been decided");
        }
        if (decision == EventStatus.PENDING) {
            throw new IllegalArgumentException("A final decision is required");
        }
        return new ApplicationEvent(
                id,
                applicationId,
                actorUserId,
                details,
                decision,
                createdAt,
                Checks.required(at, "at")
        );
    }

    private static void ensureBelongsToApplication(
            EventDetails details,
            UUID applicationId
    ) {
        UUID owner = switch (details) {
            case StatusChanged ignored -> applicationId;
            case ResultChanged ignored -> applicationId;
            case StepAdded added -> added.step().applicationId();
            case StepRemoved removed -> removed.step().applicationId();
            case StepChanged changed -> changed.next().applicationId();
        };
        if (!applicationId.equals(owner)) {
            throw new IllegalArgumentException("The step belongs to another application");
        }
    }
}
