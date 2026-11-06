package com.kairos.controller;

import com.kairos.domain.Application;
import com.kairos.domain.ApplicationStatus;
import com.kairos.service.ApplicationService;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    public record ChangeStatusRequest(ApplicationStatus status) {}

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
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
}
