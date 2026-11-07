package com.kairos.service;

import com.kairos.domain.Application;
import com.kairos.domain.ApplicationChange;
import com.kairos.domain.ApplicationEvent;
import com.kairos.domain.ApplicationResult;
import com.kairos.domain.ApplicationStep;
import com.kairos.domain.ApplicationStepFormat;
import com.kairos.domain.ApplicationStepStatus;
import com.kairos.domain.ApplicationStepType;
import com.kairos.domain.ApplicationStatus;
import com.kairos.repository.ApplicationEventRepository;
import com.kairos.repository.ApplicationRepository;
import com.kairos.repository.ApplicationStepRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationEventRepository applicationEventRepository;
    private final ApplicationStepRepository applicationStepRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            ApplicationEventRepository applicationEventRepository,
            ApplicationStepRepository applicationStepRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.applicationEventRepository = applicationEventRepository;
        this.applicationStepRepository = applicationStepRepository;
    }

    @Transactional
    public Application changeStatus(
            UUID applicationId,
            ApplicationStatus nextStatus
    ) {
        Application application = findApplication(applicationId);

        Application updated = application.changeStatus(nextStatus);
        ApplicationChange.StatusChanged change = new ApplicationChange.StatusChanged(
                application.status(),
                updated.status()
        );
        ApplicationEvent event = new ApplicationEvent(
                UUID.randomUUID(),
                application.id(),
                application.userId(),
                change,
                Instant.now(),
                null
        );

        Application saved = applicationRepository.save(updated);
        applicationEventRepository.save(event);

        return saved;
    }

    @Transactional
    public Application changeResult(UUID applicationId, ApplicationResult nextResult) {
        Application application = findApplication(applicationId);
        Application updated = application.changeResult(nextResult);
        ApplicationChange.ResultChanged change = new ApplicationChange.ResultChanged(
                application.result(),
                updated.result()
        );
        ApplicationEvent event = new ApplicationEvent(
                UUID.randomUUID(),
                application.id(),
                application.userId(),
                change,
                Instant.now(),
                null
        );

        Application saved = applicationRepository.save(updated);
        applicationEventRepository.save(event);

        return saved;
    }

    @Transactional
    public ApplicationStep addStep(
            UUID applicationId,
            ApplicationStepType type,
            String title,
            String objective,
            ApplicationStepFormat format,
            ZonedDateTime scheduledAt,
            List<String> participants,
            String practicalInformation,
            String preparationNotes,
            String debrief,
            ApplicationStepStatus status
    ) {
        Application application = findApplication(applicationId);
        ApplicationStep step = new ApplicationStep(
                UUID.randomUUID(),
                application.id(),
                type,
                title,
                applicationStepRepository.nextPosition(application.id()),
                objective,
                format,
                scheduledAt,
                participants,
                practicalInformation,
                preparationNotes,
                debrief,
                status == null ? ApplicationStepStatus.TO_SCHEDULE : status
        );
        ApplicationChange.StepAdded change = new ApplicationChange.StepAdded(step);
        ApplicationEvent event = new ApplicationEvent(
                UUID.randomUUID(),
                application.id(),
                application.userId(),
                change,
                Instant.now(),
                null
        );

        ApplicationStep saved = applicationStepRepository.save(step);
        applicationEventRepository.save(event);

        return saved;
    }

    @Transactional
    public ApplicationStep changeStep(
            UUID applicationId,
            UUID stepId,
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
        Application application = findApplication(applicationId);
        ApplicationStep step = applicationStepRepository.findById(applicationId, stepId)
                .orElseThrow(() -> new ApplicationStepNotFoundException(
                        "Unknown application step: " + stepId
                ));

        ApplicationStep updated = step.change(
                type,
                title,
                position,
                objective,
                format,
                scheduledAt,
                participants,
                practicalInformation,
                preparationNotes,
                debrief,
                status
        );
        ApplicationChange.StepChanged change = new ApplicationChange.StepChanged(step, updated);
        ApplicationEvent event = new ApplicationEvent(
                UUID.randomUUID(),
                application.id(),
                application.userId(),
                change,
                Instant.now(),
                null
        );

        ApplicationStep saved = applicationStepRepository.update(updated);
        applicationEventRepository.save(event);

        return saved;
    }

    private Application findApplication(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Unknown application: " + applicationId
                ));
    }
}
