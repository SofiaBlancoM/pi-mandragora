package es.cifpcarlos3.pimandragora.application.auth;

import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthSessionDto;
import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthUserDto;

public interface AuthClient {
    AuthUserDto getCurrentUser();

    AuthSessionDto login(String email, String password);

    void logout();
}
