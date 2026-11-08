package com.kairos.domain;

import com.kairos.validation.Checks;

import java.net.URI;
import java.util.UUID;

public record Company(UUID id, String name, URI website) {
    public Company {
        Checks.required(id, "id");
        Checks.text(name, "name");
    }
}

