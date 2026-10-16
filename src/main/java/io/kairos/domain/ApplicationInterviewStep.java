package io.kairos.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ApplicationInterviewStep(
        UUID id,
        UUID applicationId,
        String title,
        int position,
        String objective,
        String format,
        ZonedDateTime scheduledAt,
        List<String> participants,
        String practicalInformation,
        String preparationNotes,
        String debrief,
        InterviewStepStatus status
) {
    public ApplicationInterviewStep {
        Checks.required(id, "id");
        Checks.required(applicationId, "applicationId");
        Checks.text(title, "title");
        Checks.required(status, "status");
        participants = participants == null ? List.of() : List.copyOf(participants);
        if (position < 0) {
            throw new IllegalArgumentException("position must be non-negative");
        }
        if (status == InterviewStepStatus.SCHEDULED && scheduledAt == null) {
            throw new IllegalArgumentException("A scheduled step requires a date");
        }
    }
}

