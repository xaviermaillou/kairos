package com.kairos.repository;

import com.kairos.domain.Application;
import com.kairos.domain.ApplicationResult;
import com.kairos.domain.ApplicationStatus;
import com.kairos.domain.JobOfferSnapshot;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Types;
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
                        SELECT a.id,
                               a.user_id,
                               a.sent_at,
                               a.channel,
                               a.status,
                               a.result,
                               snapshot.id AS snapshot_id,
                               snapshot.job_offer_id,
                               snapshot.title AS offer_title,
                               snapshot.company_name AS offer_company_name,
                               snapshot.content AS offer_content,
                               snapshot.location AS offer_location,
                               snapshot.country AS offer_country,
                               snapshot.work_arrangement AS offer_work_arrangement,
                               snapshot.captured_at
                        FROM applications a
                        JOIN job_offer_snapshots snapshot
                          ON snapshot.id = a.job_offer_snapshot_id
                        WHERE a.id = :id
                        """)
                .param("id", id)
                .query((resultSet, rowNumber) -> new Application(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getObject("user_id", UUID.class),
                        resultSet.getObject("sent_at", OffsetDateTime.class).toInstant(),
                        resultSet.getString("channel"),
                        ApplicationStatus.valueOf(resultSet.getString("status")),
                        resultSet.getString("result") == null
                                ? null
                                : ApplicationResult.valueOf(resultSet.getString("result")),
                        new JobOfferSnapshot(
                                resultSet.getObject("snapshot_id", UUID.class),
                                resultSet.getObject("job_offer_id", UUID.class),
                                resultSet.getString("offer_title"),
                                resultSet.getString("offer_company_name"),
                                resultSet.getString("offer_content"),
                                resultSet.getString("offer_location"),
                                resultSet.getString("offer_country"),
                                resultSet.getString("offer_work_arrangement"),
                                resultSet.getObject("captured_at", OffsetDateTime.class).toInstant()
                        )
                ))
                .optional();
    }

    public Application save(Application application) {
        jdbcClient.sql("""
                        INSERT INTO applications (
                            id,
                            user_id,
                            job_offer_snapshot_id,
                            sent_at,
                            channel,
                            status,
                            result
                        ) VALUES (
                            :id,
                            :userId,
                            :jobOfferSnapshotId,
                            :sentAt,
                            :channel,
                            :status,
                            :result
                        )
                        ON CONFLICT (id) DO UPDATE SET
                            user_id = EXCLUDED.user_id,
                            job_offer_snapshot_id = EXCLUDED.job_offer_snapshot_id,
                            sent_at = EXCLUDED.sent_at,
                            channel = EXCLUDED.channel,
                            status = EXCLUDED.status,
                            result = EXCLUDED.result
                        """)
                .param("id", application.id())
                .param("userId", application.userId())
                .param("jobOfferSnapshotId", application.offerSnapshot().id())
                .param(
                        "sentAt",
                        OffsetDateTime.ofInstant(application.sentAt(), ZoneOffset.UTC)
                )
                .param("channel", application.channel())
                .param("status", application.status().name())
                .param(
                        "result",
                        application.result() == null ? null : application.result().name(),
                        Types.VARCHAR
                )
                .update();

        return application;
    }
}
