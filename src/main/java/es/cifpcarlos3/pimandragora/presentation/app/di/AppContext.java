package es.cifpcarlos3.pimandragora.presentation.app.di;

import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthSessionDto;
import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthUserDto;
import es.cifpcarlos3.pimandragora.application.authors.usecases.FindAllAuthorsUseCase;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.CreateAuthorUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.CreateBookUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.delete.DeleteBookUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.GetBooksUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbyid.GetBookByIdUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.UpdateBookUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.FindAllCategoriesUseCase;
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlResolver;
import es.cifpcarlos3.pimandragora.application.userprofile.usecases.getcurrentuser.GetCurrentUserUseCase;
import es.cifpcarlos3.pimandragora.infrastructure.auth.SupabaseAuthClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors.SupabaseAuthorCommandRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors.SupabaseAuthorQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books.SupabaseBookQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books.SupabaseBookRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories.SupabaseCategoryQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.users.SupabaseUserProfileRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.StorageApi;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseHttpClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseSession;
import es.cifpcarlos3.pimandragora.infrastructure.external.WikipediaClient;
import es.cifpcarlos3.pimandragora.infrastructure.images.SupabaseBookCoverImageStorage;
import es.cifpcarlos3.pimandragora.infrastructure.images.SupabaseCoverImageUrlResolver;
import es.cifpcarlos3.pimandragora.presentation.app.config.AppConfig;
import es.cifpcarlos3.pimandragora.presentation.app.config.PropertyKey;
import es.cifpcarlos3.pimandragora.presentation.app.constants.ConfigConstants;
import es.cifpcarlos3.pimandragora.presentation.authors.viewmodels.AuthorsViewModel;
import es.cifpcarlos3.pimandragora.presentation.books.viewmodels.BooksViewModel;
import lombok.AccessLevel;
import lombok.Getter;

public final class AppContext {
    // -------------------------------------------------------------------------
    // Singleton access
    // -------------------------------------------------------------------------
    private static volatile AppContext INSTANCE;

    // -------------------------------------------------------------------------
    // Infra (hidden)
    // -------------------------------------------------------------------------
    @Getter(AccessLevel.NONE)
    private final SupabaseHttpClient supabase;
    @Getter(AccessLevel.NONE)
    private final PostgrestApi postgrest;
    @Getter(AccessLevel.NONE)
    private final StorageApi storageApi;
    @Getter(AccessLevel.NONE)
    private final WikipediaClient wikipediaClient;

    // -------------------------------------------------------------------------
    // App services / use cases / Repositories (exposed)
    // -------------------------------------------------------------------------
    @Getter
    private final SupabaseAuthClient authClient;
    @Getter
    private final GetBooksUseCase getBooksUseCase;
    @Getter
    private final FindAllCategoriesUseCase findAllCategoriesUseCase;
    @Getter
    private final GetBookByIdUseCase getBookByIdUseCase;
    @Getter
    private final CreateBookUseCase createBookUseCase;
    @Getter
    private final UpdateBookUseCase updateBookUseCase;
    @Getter
    private final DeleteBookUseCase deleteBookUseCase;
    @Getter
    private final FindAllAuthorsUseCase findAllAuthorsUseCase;
    @Getter
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    // Campo añadido para resolver el error del repositorio de autores
    @Getter
    private final SupabaseAuthorCommandRepository authorCommandRepository;

    // -------------------------------------------------------------------------
    // .Properties configuration
    // -------------------------------------------------------------------------
    int pageSize = AppConfig.getInt(PropertyKey.BOOKS_PAGE_SIZE, ConfigConstants.DEFAULT_BOOKS_PAGE_SIZE);
    int signedUrlTimeinSeconds = AppConfig.getInt(PropertyKey.SIGNED_URL_TTL_SECONDS, ConfigConstants.DEFAULT_SIGNED_URL_TTL);

    // -------------------------------------------------------------------------
    // Session (mutable)
    // -------------------------------------------------------------------------
    private volatile SessionContext session;

    // -------------------------------------------------------------------------
    // Wiring
    // -------------------------------------------------------------------------
    private AppContext() {
        // 1. Core Infra
        this.supabase = new SupabaseHttpClient();
        this.postgrest = new PostgrestApi(supabase);
        this.storageApi = new StorageApi(supabase);
        this.wikipediaClient = new WikipediaClient();

        // 2. Repositories
        var bookQueryRepository = new SupabaseBookQueryRepository(postgrest);
        var bookRepository = new SupabaseBookRepository(postgrest);
        var authorQueryRepository = new SupabaseAuthorQueryRepository(postgrest);

        // CORRECCIÓN: Se asigna al campo de la clase, no a una variable local
        this.authorCommandRepository = new SupabaseAuthorCommandRepository(postgrest);

        var categoryQueryRepository = new SupabaseCategoryQueryRepository(postgrest);
        var userProfileRepository = new SupabaseUserProfileRepository(postgrest);
        var coverStorage = new SupabaseBookCoverImageStorage(storageApi);

        // 3. Auth
        this.authClient = new SupabaseAuthClient(supabase);

        // 4. Use cases
        this.getBooksUseCase = new GetBooksUseCase(bookQueryRepository);
        this.findAllCategoriesUseCase = new FindAllCategoriesUseCase(categoryQueryRepository);
        this.getBookByIdUseCase = new GetBookByIdUseCase(bookRepository);
        this.createBookUseCase = new CreateBookUseCase(bookRepository, coverStorage);
        this.updateBookUseCase = new UpdateBookUseCase(bookRepository, coverStorage);
        this.deleteBookUseCase = new DeleteBookUseCase(bookRepository, coverStorage);

        this.findAllAuthorsUseCase = new FindAllAuthorsUseCase(
                authorQueryRepository,
                authorCommandRepository,
                wikipediaClient
        );

        this.getCurrentUserUseCase = new GetCurrentUserUseCase(authClient, userProfileRepository);
    }

    public static AppContext get() {
        if (INSTANCE == null) throw new IllegalStateException("AppContext not initialized");
        return INSTANCE;
    }

    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new AppContext();
        }
    }

    // -------------------------------------------------------------------------
    // Session lifecycle
    // -------------------------------------------------------------------------
    public boolean isLoggedIn() {
        return session != null && SupabaseSession.hasToken();
    }

    public AuthSessionDto login(String email, String password) {
        AuthSessionDto s = authClient.login(email, password);

        AuthUserDto user = authClient.getCurrentUser();
        CoverImageUrlResolver coverResolver = new SupabaseCoverImageUrlResolver(storageApi, this.signedUrlTimeinSeconds);

        replaceSession(new SessionContext(user, coverResolver));
        return s;
    }

    private void replaceSession(SessionContext newSession) {
        if (session != null) session.dispose();
        session = newSession;
    }

    public void logout() {
        try {
            authClient.logout();
        } finally {
            clearSession();
            SupabaseSession.clear();
        }
    }

    private void clearSession() {
        if (session != null) {
            session.dispose();
            session = null;
        }
    }

    // -------------------------------------------------------------------------
    // Factories (page scope)
    // -------------------------------------------------------------------------
    public BooksViewModel newBooksViewModel() {
        return new BooksViewModel(getBooksUseCase, session().coverResolver(), this.pageSize);
    }

    public AuthorsViewModel newAuthorsViewModel() {
        return new AuthorsViewModel(findAllAuthorsUseCase);
    }

    public CreateAuthorUseCase getCreateAuthorUseCase() {
        // Ahora authorCommandRepository es accesible
        return new CreateAuthorUseCase(authorCommandRepository);
    }

    public SessionContext session() {
        if (session == null) throw new IllegalStateException("No active session (user not logged in)");
        return session;
    }
}