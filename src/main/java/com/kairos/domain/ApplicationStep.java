package com.kairos.domain;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record ApplicationStep(
        UUID id,
        UUID applicationId,
        ApplicationStepType type,
        String title,
        int position,
        String objective,
        ApplicationStepFormat format,
        ZonedDateTime scheduledAt,
        List<String> participants,
        String practicalInformation,
        String preparationNotes,
        String debrief,
        ApplicationStepStatus status
) {
    public ApplicationStep {
        Checks.required(id, "id");
        Checks.required(applicationId, "applicationId");
        Checks.required(type, "type");
        Checks.text(title, "title");
        Checks.required(status, "status");
        participants = participants == null ? List.of() : List.copyOf(participants);
        if (position < 0) {
            throw new IllegalArgumentException("position must be non-negative");
        }
        if (status == ApplicationStepStatus.SCHEDULED && scheduledAt == null) {
            throw new IllegalArgumentException("A scheduled step requires a date");
        }
    }

    public ApplicationStep change(
            ApplicationStepType nextType,
            String nextTitle,
            int nextPosition,
            String nextObjective,
            ApplicationStepFormat nextFormat,
            ZonedDateTime nextScheduledAt,
            List<String> nextParticipants,
            String nextPracticalInformation,
            String nextPreparationNotes,
            String nextDebrief,
            ApplicationStepStatus nextStatus
    ) {
        ApplicationStep changed = new ApplicationStep(
                id,
                applicationId,
                nextType,
                nextTitle,
                nextPosition,
                nextObjective,
                nextFormat,
                nextScheduledAt,
                nextParticipants,
                nextPracticalInformation,
                nextPreparationNotes,
                nextDebrief,
                nextStatus
        );

        if (equals(changed)) {
            throw new IllegalStateException("Application step has no changes");
        }

        return changed;
    }
}
