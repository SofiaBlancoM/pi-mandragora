package es.cifpcarlos3.pimandragora.application.userprofile.usecases.getbyid;

import es.cifpcarlos3.pimandragora.application.userprofile.repositories.UserProfileRepository;
import es.cifpcarlos3.pimandragora.application.userprofile.usecases.getbyid.dtos.GetUserByIdResponse;
import es.cifpcarlos3.pimandragora.domain.entities.User;

import java.util.UUID;

public record GetUserByIdUseCase(UserProfileRepository repository) {

    public GetUserByIdUseCase {
        if (repository == null) throw new IllegalArgumentException("repository is required");
    }

    public GetUserByIdResponse execute(UUID id) {
        if (id == null) throw new IllegalArgumentException("id is required");

        User user = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user: " + id));

        return new GetUserByIdResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayname(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
