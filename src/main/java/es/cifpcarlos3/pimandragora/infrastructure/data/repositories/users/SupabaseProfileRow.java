package es.cifpcarlos3.pimandragora.infrastructure.data.repositories.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record SupabaseProfileRow(
        UUID id,
        String username,
        @JsonProperty("display_name") String displayName,
        String role,
        @JsonProperty("created_at") Instant createdAt
) {
}
