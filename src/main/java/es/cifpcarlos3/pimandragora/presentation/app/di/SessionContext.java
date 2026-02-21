package es.cifpcarlos3.pimandragora.presentation.app.di;

import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthUserDto;
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlGenerator;

import java.util.Objects;

/**
 * Reinicia la caché de la generación de urls para las imágenes del bucket
 *
 * @param currentUser
 * @param coverResolver
 */
public record SessionContext(AuthUserDto currentUser, CoverImageUrlGenerator coverResolver) {

    public SessionContext(AuthUserDto currentUser, CoverImageUrlGenerator coverResolver) {
        this.currentUser = Objects.requireNonNull(currentUser, "currentUser is required");
        this.coverResolver = Objects.requireNonNull(coverResolver, "coverResolver is required");
    }

    public void dispose() {
        coverResolver.clear();
    }
}
