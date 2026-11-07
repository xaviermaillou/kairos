package com.kairos.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kairos.domain.ApplicationStep;
import com.kairos.domain.ApplicationStepFormat;
import com.kairos.domain.ApplicationStepStatus;
import com.kairos.domain.ApplicationStepType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.io.UncheckedIOException;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ApplicationStepRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper;

    public ApplicationStepRepository(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        this.jdbcClient = jdbcClient;
        this.objectMapper = objectMapper;
    }

    public Optional<ApplicationStep> findById(UUID applicationId, UUID stepId) {
        return jdbcClient.sql("""
                        SELECT id,
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
                               debrief,
                               status
                        FROM application_steps
                        WHERE id = :stepId
                          AND application_id = :applicationId
                        """)
                .param("stepId", stepId)
                .param("applicationId", applicationId)
                .query((resultSet, rowNumber) -> new ApplicationStep(
                        resultSet.getObject("id", UUID.class),
                        resultSet.getObject("application_id", UUID.class),
                        ApplicationStepType.valueOf(resultSet.getString("type")),
                        resultSet.getString("title"),
                        resultSet.getInt("position_index"),
                        resultSet.getString("objective"),
                        optionalFormat(resultSet.getString("format")),
                        optionalDate(resultSet.getObject("scheduled_at", OffsetDateTime.class)),
                        participants(resultSet.getString("participants")),
                        resultSet.getString("practical_information"),
                        resultSet.getString("preparation_notes"),
                        resultSet.getString("debrief"),
                        ApplicationStepStatus.valueOf(resultSet.getString("status"))
                ))
                .optional();
    }

    public int nextPosition(UUID applicationId) {
        return jdbcClient.sql("""
                        SELECT COALESCE(MAX(position_index), -1) + 1
                        FROM application_steps
                        WHERE application_id = :applicationId
                        """)
                .param("applicationId", applicationId)
                .query(Integer.class)
                .single();
    }

    public ApplicationStep save(ApplicationStep step) {
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
                            debrief,
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
                            :debrief,
                            :status
                        )
                        """)
                .param("id", step.id())
                .param("applicationId", step.applicationId())
                .param("type", step.type().name())
                .param("title", step.title())
                .param("position", step.position())
                .param("objective", step.objective())
                .param("format", step.format() == null ? null : step.format().name())
                .param("scheduledAt", step.scheduledAt() == null
                        ? null
                        : step.scheduledAt().toOffsetDateTime())
                .param("participants", serializeParticipants(step.participants()))
                .param("practicalInformation", step.practicalInformation())
                .param("preparationNotes", step.preparationNotes())
                .param("debrief", step.debrief())
                .param("status", step.status().name())
                .update();

        return step;
    }

    public ApplicationStep update(ApplicationStep step) {
        jdbcClient.sql("""
                        UPDATE application_steps
                        SET type = :type,
                            title = :title,
                            position_index = :position,
                            objective = :objective,
                            format = :format,
                            scheduled_at = :scheduledAt,
                            participants = CAST(:participants AS JSONB),
                            practical_information = :practicalInformation,
                            preparation_notes = :preparationNotes,
                            debrief = :debrief,
                            status = :status
                        WHERE id = :id
                          AND application_id = :applicationId
                        """)
                .param("type", step.type().name())
                .param("title", step.title())
                .param("position", step.position())
                .param("objective", step.objective())
                .param("format", step.format() == null ? null : step.format().name())
                .param("scheduledAt", step.scheduledAt() == null
                        ? null
                        : step.scheduledAt().toOffsetDateTime())
                .param("participants", serializeParticipants(step.participants()))
                .param("practicalInformation", step.practicalInformation())
                .param("preparationNotes", step.preparationNotes())
                .param("debrief", step.debrief())
                .param("status", step.status().name())
                .param("id", step.id())
                .param("applicationId", step.applicationId())
                .update();

        return step;
    }

    private static ApplicationStepFormat optionalFormat(String format) {
        return format == null ? null : ApplicationStepFormat.valueOf(format);
    }

    private static ZonedDateTime optionalDate(OffsetDateTime date) {
        return date == null ? null : date.toZonedDateTime();
    }

    private List<String> participants(String participants) {
        if (participants == null) {
            return List.of();
        }

        try {
            return objectMapper.readValue(
                    participants,
                    new TypeReference<List<String>>() {}
            );
        } catch (JsonProcessingException exception) {
            throw new UncheckedIOException("Could not read application step participants", exception);
        }
    }

    private String serializeParticipants(List<String> participants) {
        try {
            return objectMapper.writeValueAsString(participants);
        } catch (JsonProcessingException exception) {
            throw new UncheckedIOException("Could not serialize application step participants", exception);
        }
    }
}
