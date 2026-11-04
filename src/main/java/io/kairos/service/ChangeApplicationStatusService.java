package io.kairos.service;

import io.kairos.domain.Application;
import io.kairos.domain.ApplicationStatus;
import io.kairos.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChangeApplicationStatusService {

    private final ApplicationRepository applicationRepository;

    public ChangeApplicationStatusService(ApplicationRepository applicationRepository) {
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
