package es.cifpcarlos3.pimandragora.application.auth.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthSessionDto(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") int expiresIn,
        @JsonProperty("expires_at") int expiresAt
) {
}
