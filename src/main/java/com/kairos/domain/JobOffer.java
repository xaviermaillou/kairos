package com.kairos.domain;

import com.kairos.validation.Checks;

import java.util.UUID;

public record JobOffer(
        UUID id,
        UUID companyId
) {
    public JobOffer {
        Checks.required(id, "id");
        Checks.required(companyId, "companyId");
    }
}
