package io.kairos.repository;

import io.kairos.domain.Application;
import io.kairos.domain.ApplicationStatus;
import io.kairos.domain.JobOfferSnapshot;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ApplicationRepository {

    private final JdbcClient jdbcClient;

    public ApplicationRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Application> findById(UUID id) {
        return jdbcClient.sql("""
                        SELECT id,
                               user_id,
                               job_offer_id,
                               sent_at,
                               channel,
                               status,
                               offer_title,
                               offer_company_name,
                               offer_content,
                               offer_location,
                               offer_country,
                               offer_work_arrangement
                        FROM applications
                        WHERE id = :id
                        """)
                .param("id", id)
                .query((resultSet, rowNumber) -> new Application(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getObject("user_id", UUID.class),
                        resultSet.getObject("job_offer_id", UUID.class),
                        resultSet.getObject("sent_at", OffsetDateTime.class).toInstant(),
                        resultSet.getString("channel"),
                        ApplicationStatus.valueOf(resultSet.getString("status")),
                        new JobOfferSnapshot(
                                resultSet.getString("offer_title"),
                                resultSet.getString("offer_company_name"),
                                resultSet.getString("offer_content"),
                                resultSet.getString("offer_location"),
                                resultSet.getString("offer_country"),
                                resultSet.getString("offer_work_arrangement")
                        )
                ))
                .optional();
    }

    public Application save(Application application) {
        jdbcClient.sql("""
                        INSERT INTO applications (
                            id,
                            user_id,
                            job_offer_id,
                            sent_at,
                            channel,
                            status,
                            offer_title,
                            offer_company_name,
                            offer_content,
                            offer_location,
                            offer_country,
                            offer_work_arrangement
                        ) VALUES (
                            :id,
                            :userId,
                            :jobOfferId,
                            :sentAt,
                            :channel,
                            :status,
                            :offerTitle,
                            :offerCompanyName,
                            :offerContent,
                            :offerLocation,
                            :offerCountry,
                            :offerWorkArrangement
                        )
                        ON CONFLICT (id) DO UPDATE SET
                            user_id = EXCLUDED.user_id,
                            job_offer_id = EXCLUDED.job_offer_id,
                            sent_at = EXCLUDED.sent_at,
                            channel = EXCLUDED.channel,
                            status = EXCLUDED.status,
                            offer_title = EXCLUDED.offer_title,
                            offer_company_name = EXCLUDED.offer_company_name,
                            offer_content = EXCLUDED.offer_content,
                            offer_location = EXCLUDED.offer_location,
                            offer_country = EXCLUDED.offer_country,
                            offer_work_arrangement = EXCLUDED.offer_work_arrangement
                        """)
                .param("id", application.id())
                .param("userId", application.userId())
                .param("jobOfferId", application.jobOfferId())
                .param(
                        "sentAt",
                        OffsetDateTime.ofInstant(application.sentAt(), ZoneOffset.UTC)
                )
                .param("channel", application.channel())
                .param("status", application.status().name())
                .param("offerTitle", application.offerSnapshot().title())
                .param("offerCompanyName", application.offerSnapshot().companyName())
                .param("offerContent", application.offerSnapshot().content())
                .param("offerLocation", application.offerSnapshot().location())
                .param("offerCountry", application.offerSnapshot().country())
                .param("offerWorkArrangement", application.offerSnapshot().workArrangement())
                .update();

        return application;
    }
}
