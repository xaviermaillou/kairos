package com.kairos.domain;

import java.util.Objects;
import java.util.UUID;

final class Checks {
    private Checks() {
    }

    static <T> T required(T value, String name) {
        return Objects.requireNonNull(value, name + " must not be null");
    }

    static String text(String value, String name) {
        required(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    static void belongsTo(ApplicationEvent.EventDetails details, UUID applicationId) {
        UUID owner = switch (details) {
            case ApplicationEvent.StatusChanged ignored -> applicationId;
            case ApplicationEvent.ResultChanged ignored -> applicationId;
            case ApplicationEvent.StepAdded added -> added.step().applicationId();
            case ApplicationEvent.StepRemoved removed -> removed.step().applicationId();
            case ApplicationEvent.StepChanged changed -> changed.next().applicationId();
        };
        if (!applicationId.equals(owner)) {
            throw new IllegalArgumentException("The step belongs to another application");
        }
    }
}

