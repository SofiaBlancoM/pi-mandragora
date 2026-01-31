package es.cifpcarlos3.pimandragora.application.userprofile.usecases.getcurrentuser;

import es.cifpcarlos3.pimandragora.application.userprofile.repositories.UserProfileRepository;
import es.cifpcarlos3.pimandragora.application.userprofile.usecases.getcurrentuser.dtos.GetCurrentUserResponse;
import es.cifpcarlos3.pimandragora.domain.entities.User;
import es.cifpcarlos3.pimandragora.infrastructure.auth.SupabaseAuthClient;

public record GetCurrentUserUseCase(SupabaseAuthClient authClient, UserProfileRepository profileRepository) {

    public GetCurrentUserUseCase {
        if (authClient == null) throw new IllegalArgumentException("authClient is required");
        if (profileRepository == null) throw new IllegalArgumentException("profileRepository is required");
    }

    public GetCurrentUserResponse execute() {
        var authUser = authClient.getCurrentUser();

        User profile = profileRepository.findById(authUser.id())
                .orElse(User.builder()
                        .id(authUser.id())
                        .username(null)
                        .displayname(null)
                        .role("USER")
                        .email(null)
                        .build());

        return new GetCurrentUserResponse(
                authUser.id(),
                authUser.email(),              // ✅ from auth
                profile.getUsername(),
                profile.getDisplayname(),
                profile.getRole()
        );
    }
}
