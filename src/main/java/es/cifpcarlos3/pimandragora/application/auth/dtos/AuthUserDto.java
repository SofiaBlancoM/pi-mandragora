package es.cifpcarlos3.pimandragora.application.auth.dtos;

import java.util.UUID;

public record AuthUserDto(
        UUID id,
        String email
) {
}
