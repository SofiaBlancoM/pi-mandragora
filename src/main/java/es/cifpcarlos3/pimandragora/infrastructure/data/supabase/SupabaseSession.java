package es.cifpcarlos3.pimandragora.infrastructure.data.supabase;

import lombok.Getter;
import lombok.Setter;

/**
 * Clase que guarda de manera estática el access token del usuario al loguearse
 */
public final class SupabaseSession {

    @Getter
    @Setter
    private static volatile String accessToken;

    private SupabaseSession() {
    }

    public static void clear() {
        accessToken = null;
    }

    public static boolean hasToken() {
        return accessToken != null && !accessToken.isBlank();
    }
}
