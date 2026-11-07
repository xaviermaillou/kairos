package com.kairos.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    private static final UUID USER_ID = UUID.fromString(
            "10000000-0000-0000-0000-000000000001"
    );
    private static final UUID COMPANY_ID = UUID.fromString(
            "20000000-0000-0000-0000-000000000001"
    );
    private static final UUID JOB_OFFER_ID = UUID.fromString(
            "30000000-0000-0000-0000-000000000001"
    );
    private static final UUID SNAPSHOT_ID = UUID.fromString(
            "40000000-0000-0000-0000-000000000001"
    );
    private static final UUID APPLICATION_ID = UUID.fromString(
            "50000000-0000-0000-0000-000000000001"
    );
    private static final UUID APPLICATION_STEP_ID = UUID.fromString(
            "60000000-0000-0000-0000-000000000001"
    );
    private static final OffsetDateTime CREATED_AT = OffsetDateTime.ofInstant(
            Instant.parse("2026-11-07T09:00:00Z"),
            ZoneOffset.UTC
    );

    private final JdbcClient jdbcClient;

    public DevDataSeeder(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments arguments) {
        seedUser();
        seedCompany();
        seedJobOffer();
        seedJobOfferSnapshot();
        seedApplication();
        seedApplicationStep();
    }

    private void seedUser() {
        jdbcClient.sql("""
                        INSERT INTO users (id, contact_email)
                        VALUES (:id, :contactEmail)
                        ON CONFLICT (id) DO NOTHING
                        """)
                .param("id", USER_ID)
                .param("contactEmail", "demo@kairos.com")
                .update();
    }

    private void seedCompany() {
        jdbcClient.sql("""
                        INSERT INTO companies (id, name, website)
                        VALUES (:id, :name, :website)
                        ON CONFLICT (id) DO NOTHING
                        """)
                .param("id", COMPANY_ID)
                .param("name", "Kairos Labs")
                .param("website", "https://example.com")
                .update();
    }

    private void seedJobOffer() {
        jdbcClient.sql("""
                        INSERT INTO job_offers (id, company_id)
                        VALUES (:id, :companyId)
                        ON CONFLICT (id) DO NOTHING
                        """)
                .param("id", JOB_OFFER_ID)
                .param("companyId", COMPANY_ID)
                .update();
    }

    private void seedJobOfferSnapshot() {
        jdbcClient.sql("""
                        INSERT INTO job_offer_snapshots (
                            id,
                            job_offer_id,
                            title,
                            company_name,
                            content,
                            location,
                            country,
                            work_arrangement,
                            captured_at
                        ) VALUES (
                            :id,
                            :jobOfferId,
                            :title,
                            :companyName,
                            :content,
                            :location,
                            :country,
                            :workArrangement,
                            :capturedAt
                        )
                        ON CONFLICT (id) DO NOTHING
                        """)
                .param("id", SNAPSHOT_ID)
                .param("jobOfferId", JOB_OFFER_ID)
                .param("title", "Backend Java Developer")
                .param("companyName", "Kairos Labs")
                .param("content", "Build and maintain the Kairos backend.")
                .param("location", "Luxembourg")
                .param("country", "Luxembourg")
                .param("workArrangement", "HYBRID")
                .param("capturedAt", CREATED_AT)
                .update();
    }

    private void seedApplication() {
        jdbcClient.sql("""
                        INSERT INTO applications (
                            id,
                            user_id,
                            job_offer_snapshot_id,
                            sent_at,
                            channel,
                            status
                        ) VALUES (
                            :id,
                            :userId,
                            :jobOfferSnapshotId,
                            :sentAt,
                            :channel,
                            :status
                        )
                        ON CONFLICT (id) DO NOTHING
                        """)
                .param("id", APPLICATION_ID)
                .param("userId", USER_ID)
                .param("jobOfferSnapshotId", SNAPSHOT_ID)
                .param("sentAt", CREATED_AT)
                .param("channel", "CAREER_PAGE")
                .param("status", "SENT")
                .update();
    }

    private void seedApplicationStep() {
        jdbcClient.sql("""
                        INSERT INTO application_steps (
                            id,
                            application_id,
                            type,
                            title,
                            position_index,
                            objective,
                            format,
                            scheduled_at,
                            participants,
                            practical_information,
                            preparation_notes,
                            status
                        ) VALUES (
                            :id,
                            :applicationId,
                            :type,
                            :title,
                            :position,
                            :objective,
                            :format,
                            :scheduledAt,
                            CAST(:participants AS JSONB),
                            :practicalInformation,
                            :preparationNotes,
                            :status
                        )
                        ON CONFLICT (id) DO NOTHING
                        """)
                .param("id", APPLICATION_STEP_ID)
                .param("applicationId", APPLICATION_ID)
                .param("type", "INTERVIEW")
                .param("title", "HR interview")
                .param("position", 0)
                .param("objective", "Discuss the role and mutual expectations.")
                .param("format", "VIDEO_CALL")
                .param("scheduledAt", CREATED_AT.plusDays(3))
                .param("participants", "[\"Alice Recruiter\"]")
                .param("practicalInformation", "Video link sent by email.")
                .param("preparationNotes", "Prepare a concise career overview.")
                .param("status", "SCHEDULED")
                .update();
    }
}
