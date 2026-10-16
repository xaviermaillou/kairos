package io.kairos.domain;

/** A typed change shared by update suggestions and applied events. */
public sealed interface ApplicationChange {
    record StatusChanged(ApplicationStatus previous, ApplicationStatus next)
            implements ApplicationChange {
        public StatusChanged {
            Checks.required(previous, "previous");
            Checks.required(next, "next");
            if (previous == next) {
                throw new IllegalArgumentException("Status must change");
            }
        }
    }

    record InterviewStepAdded(ApplicationInterviewStep step) implements ApplicationChange {
        public InterviewStepAdded {
            Checks.required(step, "step");
        }
    }

    record InterviewStepUpdated(
            ApplicationInterviewStep previous,
            ApplicationInterviewStep next
    ) implements ApplicationChange {
        public InterviewStepUpdated {
            Checks.required(previous, "previous");
            Checks.required(next, "next");
            if (!previous.id().equals(next.id())
                    || !previous.applicationId().equals(next.applicationId())) {
                throw new IllegalArgumentException("Interview step identity cannot change");
            }
        }
    }
}

