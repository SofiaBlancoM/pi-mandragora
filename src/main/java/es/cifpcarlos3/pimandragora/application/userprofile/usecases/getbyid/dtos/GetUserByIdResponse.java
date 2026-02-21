package es.cifpcarlos3.pimandragora.application.userprofile.usecases.getbyid.dtos;

import es.cifpcarlos3.pimandragora.domain.entities.User;

import java.time.Instant;
import java.util.UUID;

public record GetUserByIdResponse(
        UUID id,
        String username,
        String displayName,
        String role,// will be null unless you store it somewhere
        Instant createdAt
) {
    public static GetUserByIdResponse from(User user) {
        return new GetUserByIdResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayname(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
