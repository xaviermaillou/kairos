package io.kairos.domain;

/** Historical offer data preserved at the time an application is sent. */
public record JobOfferSnapshot(
        String title,
        String companyName,
        String content,
        String location,
        String country,
        String workArrangement
) {
    public JobOfferSnapshot {
        Checks.text(title, "title");
        Checks.text(companyName, "companyName");
        Checks.text(content, "content");
    }
}

