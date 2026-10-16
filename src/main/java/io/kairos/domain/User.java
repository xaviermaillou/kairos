package io.kairos.domain;

import java.util.UUID;

public record User(UUID id, String contactEmail) {
    public User {
        Checks.required(id, "id");
        Checks.text(contactEmail, "contactEmail");
    }
}

