package com.kairos.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.domain.ApplicationEvent;
import com.kairos.domain.ApplicationEvent.EventDetails;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Repository
public class ApplicationEventRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public ApplicationEventRepository(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    public ApplicationEvent save(ApplicationEvent event) {
        jdbcClient.sql("""
                        INSERT INTO application_events (
                            id,
                            application_id,
                            actor_user_id,
                            change_type,
                            change_payload,
                            status,
                            created_at,
                            decided_at
                        ) VALUES (
                            :id,
                            :applicationId,
                            :actorUserId,
                            :changeType,
                            CAST(:changePayload AS JSONB),
                            :status,
                            :createdAt,
                            :decidedAt
                        )
                        ON CONFLICT (id) DO UPDATE SET
                            status = EXCLUDED.status,
                            decided_at = EXCLUDED.decided_at
                        """)
                .param("id", event.id())
                .param("applicationId", event.applicationId())
                .param("actorUserId", event.actorUserId())
                .param("changeType", changeType(event.details()))
                .param("changePayload", serialize(event.details()))
                .param("status", event.status().name())
                .param(
                        "createdAt",
                        OffsetDateTime.ofInstant(event.createdAt(), ZoneOffset.UTC)
                )
                .param(
                        "decidedAt",
                        event.decidedAt() == null
                                ? null
                                : OffsetDateTime.ofInstant(event.decidedAt(), ZoneOffset.UTC),
                        Types.TIMESTAMP_WITH_TIMEZONE
                )
                .update();

        return event;
    }

    private String changeType(EventDetails details) {
        return switch (details) {
            case ApplicationEvent.StatusChanged ignored -> "STATUS_CHANGED";
            case ApplicationEvent.ResultChanged ignored -> "RESULT_CHANGED";
            case ApplicationEvent.StepAdded ignored -> "STEP_ADDED";
            case ApplicationEvent.StepRemoved ignored -> "STEP_REMOVED";
            case ApplicationEvent.StepChanged ignored -> "STEP_CHANGED";
        };
    }

    private String serialize(EventDetails details) {
        try {
            return objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException exception) {
            throw new ApplicationEventPersistenceException(
                    "Could not serialize application event",
                    exception
            );
        }
    }
}
