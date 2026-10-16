package io.kairos.domain;

import java.net.URI;
import java.util.UUID;

public record Company(UUID id, String name, URI website) {
    public Company {
        Checks.required(id, "id");
        Checks.text(name, "name");
    }
}

