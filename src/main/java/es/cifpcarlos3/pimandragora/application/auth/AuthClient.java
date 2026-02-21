package es.cifpcarlos3.pimandragora.application.auth;

import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthSessionDto;
import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthUserDto;

/**
 * Contrato para funcionalidades relacionadas con la autenticación del usuario
 */
public interface AuthClient {

    /**
     *
     * @return devuelve los datos del usuario logueado
     */
    AuthUserDto getCurrentUser();

    /**
     * Login de un usuario con email y contraseña
     *
     * @param email    email del usuario
     * @param password contraseña del usuario
     * @return el token del usuario logueado
     */
    AuthSessionDto login(String email, String password);

    /**
     * Logout del usuario logueado
     */
    void logout();
}
