package com.kairos.domain;

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
