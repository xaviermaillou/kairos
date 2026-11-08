package com.kairos.validation;

import java.util.Objects;

public final class Checks {

    private Checks() {
    }

    public static <T> T required(T value, String name) {
        return Objects.requireNonNull(value, name + " must not be null");
    }

    public static String text(String value, String name) {
        required(value, name);
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
