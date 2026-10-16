package io.kairos.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ReceivedEmail(
        UUID id,
        UUID userId,
        UUID inboundAddressId,
        String messageId,
        String inReplyTo,
        List<String> references,
        String sender,
        List<String> recipients,
        String subject,
        String textBody,
        String htmlBody,
        Instant receivedAt
) {
    public ReceivedEmail {
        Checks.required(id, "id");
        Checks.required(userId, "userId");
        Checks.required(inboundAddressId, "inboundAddressId");
        Checks.text(sender, "sender");
        Checks.required(receivedAt, "receivedAt");
        references = references == null ? List.of() : List.copyOf(references);
        recipients = recipients == null ? List.of() : List.copyOf(recipients);
    }
}

