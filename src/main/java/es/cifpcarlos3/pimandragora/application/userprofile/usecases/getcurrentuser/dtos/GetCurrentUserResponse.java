package es.cifpcarlos3.pimandragora.application.userprofile.usecases.getcurrentuser.dtos;

import java.util.UUID;

public record GetCurrentUserResponse(
        UUID id,
        String email,
        String username,
        String displayName,
        String role
) {
}
