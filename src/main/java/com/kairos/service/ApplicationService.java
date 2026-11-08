package com.kairos.service;

import com.kairos.domain.Application;
import com.kairos.domain.Application.ApplicationResult;
import com.kairos.domain.Application.ApplicationStatus;
import com.kairos.domain.ApplicationEvent;
import com.kairos.domain.ApplicationStep;
import com.kairos.domain.ApplicationStep.ApplicationStepFormat;
import com.kairos.domain.ApplicationStep.ApplicationStepStatus;
import com.kairos.domain.ApplicationStep.ApplicationStepType;
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

    @Transactional(readOnly = true)
    public Application getApplication(UUID applicationId) {
        return findApplication(applicationId);
    }

    @Transactional(readOnly = true)
    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ApplicationStep> getSteps(UUID applicationId) {
        findApplication(applicationId);
        return applicationStepRepository.findByApplicationId(applicationId);
    }

    @Transactional
    public Application changeStatus(
            UUID applicationId,
            ApplicationStatus nextStatus
    ) {
        Application application = findApplication(applicationId);

        Application updated = application.changeStatus(nextStatus);
        ApplicationEvent.StatusChanged details = new ApplicationEvent.StatusChanged(
                application.status(),
                updated.status()
        );
        ApplicationEvent event = ApplicationEvent.applied(
                application.id(),
                application.userId(),
                details,
                Instant.now()
        );

        Application saved = applicationRepository.save(updated);
        applicationEventRepository.save(event);

        return saved;
    }

    @Transactional
    public Application changeResult(UUID applicationId, ApplicationResult nextResult) {
        Application application = findApplication(applicationId);
        Application updated = application.changeResult(nextResult);
        ApplicationEvent.ResultChanged details = new ApplicationEvent.ResultChanged(
                application.result(),
                updated.result()
        );
        ApplicationEvent event = ApplicationEvent.applied(
                application.id(),
                application.userId(),
                details,
                Instant.now()
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
        ApplicationEvent.StepAdded details = new ApplicationEvent.StepAdded(step);
        ApplicationEvent event = ApplicationEvent.applied(
                application.id(),
                application.userId(),
                details,
                Instant.now()
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
        ApplicationEvent.StepChanged details = new ApplicationEvent.StepChanged(step, updated);
        ApplicationEvent event = ApplicationEvent.applied(
                application.id(),
                application.userId(),
                details,
                Instant.now()
        );

        ApplicationStep saved = applicationStepRepository.update(updated);
        applicationEventRepository.save(event);

        return saved;
    }

    @Transactional
    public void removeStep(UUID applicationId, UUID stepId) {
        Application application = findApplication(applicationId);
        ApplicationStep step = applicationStepRepository.findById(applicationId, stepId)
                .orElseThrow(() -> new ApplicationStepNotFoundException(
                        "Unknown application step: " + stepId
                ));
        ApplicationEvent.StepRemoved details = new ApplicationEvent.StepRemoved(step);
        ApplicationEvent event = ApplicationEvent.applied(
                application.id(),
                application.userId(),
                details,
                Instant.now()
        );

        applicationStepRepository.delete(step);
        applicationEventRepository.save(event);
    }

    private Application findApplication(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApplicationNotFoundException(
                        "Unknown application: " + applicationId
                ));
    }
}
