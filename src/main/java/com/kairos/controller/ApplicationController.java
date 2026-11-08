package com.kairos.controller;

import com.kairos.domain.Application;
import com.kairos.domain.Application.ApplicationResult;
import com.kairos.domain.Application.ApplicationStatus;
import com.kairos.domain.ApplicationStep;
import com.kairos.domain.ApplicationStep.ApplicationStepFormat;
import com.kairos.domain.ApplicationStep.ApplicationStepStatus;
import com.kairos.domain.ApplicationStep.ApplicationStepType;
import com.kairos.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    public record ApplicationDetails(
            Application application,
            List<ApplicationStep> steps
    ) {}

    public record ChangeStatusRequest(
            ApplicationStatus status
    ) {}

    public record ChangeResultRequest(ApplicationResult result) {}

    public record AddStepRequest(
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
    ) {}

    public record ChangeStepRequest(
            ApplicationStepType type,
            String title,
            Integer position,
            String objective,
            ApplicationStepFormat format,
            ZonedDateTime scheduledAt,
            List<String> participants,
            String practicalInformation,
            String preparationNotes,
            String debrief,
            ApplicationStepStatus status
    ) {}

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping
    public List<Application> getApplications() {
        return applicationService.getApplications();
    }

    @GetMapping("/{id}")
    public ApplicationDetails getApplication(@PathVariable UUID id) {
        return new ApplicationDetails(
                applicationService.getApplication(id),
                applicationService.getSteps(id)
        );
    }

    @PatchMapping("/{id}/status")
    public Application changeStatus(
            @PathVariable UUID id,
            @RequestBody ChangeStatusRequest request
    ) {
        if (request.status() == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        return applicationService.changeStatus(id, request.status());
    }

    @PatchMapping("/{id}/result")
    public Application changeResult(
            @PathVariable UUID id,
            @RequestBody ChangeResultRequest request
    ) {
        return applicationService.changeResult(id, request.result());
    }

    @PostMapping("/{id}/steps")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationStep addStep(
            @PathVariable UUID id,
            @RequestBody AddStepRequest request
    ) {
        if (request.type() == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }

        return applicationService.addStep(
                id,
                request.type(),
                request.title(),
                request.objective(),
                request.format(),
                request.scheduledAt(),
                request.participants(),
                request.practicalInformation(),
                request.preparationNotes(),
                request.debrief(),
                request.status()
        );
    }

    @PutMapping("/{applicationId}/steps/{stepId}")
    public ApplicationStep changeStep(
            @PathVariable UUID applicationId,
            @PathVariable UUID stepId,
            @RequestBody ChangeStepRequest request
    ) {
        if (request.type() == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
        if (request.position() == null) {
            throw new IllegalArgumentException("position must not be null");
        }
        if (request.status() == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        return applicationService.changeStep(
                applicationId,
                stepId,
                request.type(),
                request.title(),
                request.position(),
                request.objective(),
                request.format(),
                request.scheduledAt(),
                request.participants(),
                request.practicalInformation(),
                request.preparationNotes(),
                request.debrief(),
                request.status()
        );
    }

    @DeleteMapping("/{applicationId}/steps/{stepId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStep(
            @PathVariable UUID applicationId,
            @PathVariable UUID stepId
    ) {
        applicationService.removeStep(applicationId, stepId);
    }
}
