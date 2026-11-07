package com.kairos.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.domain.ApplicationChange;
import com.kairos.domain.ApplicationEvent;
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
                            occurred_at,
                            suggestion_id
                        ) VALUES (
                            :id,
                            :applicationId,
                            :actorUserId,
                            :changeType,
                            CAST(:changePayload AS JSONB),
                            :occurredAt,
                            :suggestionId
                        )
                        """)
                .param("id", event.id())
                .param("applicationId", event.applicationId())
                .param("actorUserId", event.actorUserId())
                .param("changeType", changeType(event.change()))
                .param("changePayload", serialize(event.change()))
                .param(
                        "occurredAt",
                        OffsetDateTime.ofInstant(event.occurredAt(), ZoneOffset.UTC)
                )
                .param("suggestionId", event.suggestionId(), Types.OTHER)
                .update();

        return event;
    }

    private String changeType(ApplicationChange change) {
        return switch (change) {
            case ApplicationChange.StatusChanged ignored -> "STATUS_CHANGED";
            case ApplicationChange.ResultChanged ignored -> "RESULT_CHANGED";
            case ApplicationChange.StepAdded ignored -> "STEP_ADDED";
            case ApplicationChange.StepRemoved ignored -> "STEP_REMOVED";
            case ApplicationChange.StepChanged ignored -> "STEP_CHANGED";
        };
    }

    private String serialize(ApplicationChange change) {
        try {
            return objectMapper.writeValueAsString(change);
        } catch (JsonProcessingException exception) {
            throw new ApplicationEventPersistenceException(
                    "Could not serialize application change",
                    exception
            );
        }
    }
}
