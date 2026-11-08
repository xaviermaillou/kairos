package com.kairos.domain;

import com.kairos.validation.Checks;

import java.util.UUID;

/** An address dedicated to receiving messages on behalf of a user. */
public record InboundAddress(UUID id, UUID userId, String address, boolean active) {
    public InboundAddress {
        Checks.required(id, "id");
        Checks.required(userId, "userId");
        Checks.text(address, "address");
    }
}

