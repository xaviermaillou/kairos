package io.kairos.domain;

import java.util.UUID;

/** Skills remain part of the free-form content and are analysed on demand. */
public record JobOffer(
        UUID id,
        UUID companyId,
        String title,
        String content,
        String location,
        String country,
        String workArrangement
) {
    public JobOffer {
        Checks.required(id, "id");
        Checks.required(companyId, "companyId");
        Checks.text(title, "title");
        Checks.text(content, "content");
    }
}

