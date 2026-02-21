package es.cifpcarlos3.pimandragora.infrastructure.data.supabase;

import lombok.Getter;
import lombok.Setter;

public final class SupabaseSession {

    @Getter
    @Setter
    private static volatile String accessToken;

    private SupabaseSession() {
    }

    public static boolean hasToken() {
        return accessToken != null && !accessToken.isBlank();
    }

    public static void clear() {
        accessToken = null;
    }
}
