package com.kairos.domain;

import java.util.Objects;

/** A typed change shared by update suggestions and applied events. */
public sealed interface ApplicationChange {
    record StatusChanged(
            ApplicationStatus previous,
            ApplicationStatus next
    ) implements ApplicationChange {
        public StatusChanged {
            Checks.required(previous, "previous");
            Checks.required(next, "next");
            if (previous == next) {
                throw new IllegalArgumentException("Status must change");
            }
        }
    }

    record ResultChanged(
            ApplicationResult previous,
            ApplicationResult next
    ) implements ApplicationChange {
        public ResultChanged {
            if (Objects.equals(previous, next)) {
                throw new IllegalArgumentException("Result must change");
            }
        }
    }

    record StepAdded(
            ApplicationStep step
    ) implements ApplicationChange {
        public StepAdded {
            Checks.required(step, "step");
        }
    }

    record StepRemoved(
            ApplicationStep step
    ) implements ApplicationChange {
        public StepRemoved {
            Checks.required(step, "step");
        }
    }

    record StepChanged(
            ApplicationStep previous,
            ApplicationStep next
    ) implements ApplicationChange {
        public StepChanged {
            Checks.required(previous, "previous");
            Checks.required(next, "next");
            if (!previous.id().equals(next.id())
                    || !previous.applicationId().equals(next.applicationId())) {
                throw new IllegalArgumentException("Application step identity cannot change");
            }
            if (previous.equals(next)) {
                throw new IllegalArgumentException("Application step must change");
            }
        }
    }
}
