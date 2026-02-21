package es.cifpcarlos3.pimandragora.presentation.app.di;

import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthUserDto;
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlGenerator;

import java.util.Objects;

public record SessionContext(AuthUserDto currentUser, CoverImageUrlGenerator coverResolver) {

    public SessionContext(AuthUserDto currentUser, CoverImageUrlGenerator coverResolver) {
        this.currentUser = Objects.requireNonNull(currentUser, "currentUser is required");
        this.coverResolver = Objects.requireNonNull(coverResolver, "coverResolver is required");
    }

    /**
     * Call when leaving session (logout)
     */
    public void dispose() {
        coverResolver.clear(); // clears signed-url cache
    }
}
