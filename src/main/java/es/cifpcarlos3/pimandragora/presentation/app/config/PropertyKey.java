package es.cifpcarlos3.pimandragora.presentation.app.config;

public enum PropertyKey {
    SUPABASE_URL("supabase.url", "SUPABASE_URL", true),
    SUPABASE_ANON_KEY("supabase.anonKey", "SUPABASE_ANON_KEY", true),
    STORAGE_BUCKET("supabase.storage.bucket", "SUPABASE_STORAGE_BUCKET", true),

    APP_NAME("app.name", "APP_NAME", false),

    TEST_EMAIL("test.autologin.user.email", "TEST_USER_EMAIL", false),
    TEST_PASSWORD("test.autologin.user.password", "TEST_USER_PASSWORD", false),

    BOOKS_PAGE_SIZE("books.page.size", "BOOKS_PAGE_SIZE", false),
    SIGNED_URL_TTL_SECONDS("images.signedUrl.ttlSeconds", "SIGNED_URL_TTL_SECONDS", false);

    final String propertyKey;
    final String envKey;
    final boolean required;

    PropertyKey(String propertyKey, String envKey, boolean required) {
        this.propertyKey = propertyKey;
        this.envKey = envKey;
        this.required = required;
    }
}
