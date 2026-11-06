package com.kairos.service;

import com.kairos.domain.Application;
import com.kairos.domain.ApplicationStatus;
import com.kairos.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application changeStatus(UUID applicationId, ApplicationStatus next) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown application: " + applicationId
                ));

        Application updated = application.changeStatus(next);

        return applicationRepository.save(updated);
    }
}
