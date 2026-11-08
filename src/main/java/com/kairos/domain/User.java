package com.kairos.domain;

import com.kairos.validation.Checks;

import java.util.UUID;

public record User(UUID id, String contactEmail) {
    public User {
        Checks.required(id, "id");
        Checks.text(contactEmail, "contactEmail");
    }
}

