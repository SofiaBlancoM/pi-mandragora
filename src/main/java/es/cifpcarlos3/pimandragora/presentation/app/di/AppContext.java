package es.cifpcarlos3.pimandragora.presentation.app.di;

import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthSessionDto;
import es.cifpcarlos3.pimandragora.application.auth.dtos.AuthUserDto;
import es.cifpcarlos3.pimandragora.application.authors.usecases.findallauthors.FindAllAuthorsUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.create.CreateBookUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.delete.DeleteBookUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbooks.GetBooksUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbyid.GetBookByIdUseCase;
import es.cifpcarlos3.pimandragora.application.books.usecases.update.UpdateBookUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.createcategory.CreateCategoryUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.deletecategory.DeleteCategoryUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.FindAllCategoriesUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findcategorybyid.FindCategoryByIdUseCase;
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlGenerator;
import es.cifpcarlos3.pimandragora.application.userprofile.usecases.getcurrentuser.GetCurrentUserUseCase;
import es.cifpcarlos3.pimandragora.infrastructure.auth.SupabaseAuthClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors.SupabaseAuthorQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books.SupabaseBookQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books.SupabaseBookRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories.SupabaseCategoryQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.users.SupabaseUserProfileRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgrestApi;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.StorageApi;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseHttpClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseSession;
import es.cifpcarlos3.pimandragora.infrastructure.images.SupabaseBookCoverImageStorage;
import es.cifpcarlos3.pimandragora.infrastructure.images.SupabaseCoverImageUrlGenerator;
import es.cifpcarlos3.pimandragora.presentation.app.config.AppConfig;
import es.cifpcarlos3.pimandragora.presentation.app.config.PropertyKey;
import es.cifpcarlos3.pimandragora.presentation.app.constants.ConfigConstants;
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
    // -------------------------------------------------------------------------
    // App services / use cases (exposed)
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
        this.supabase = new SupabaseHttpClient();
        this.postgrest = new PostgrestApi(supabase);
        this.storageApi = new StorageApi(supabase);

        // Repositories are local (no need to expose as fields)
        var bookQueryRepository = new SupabaseBookQueryRepository(postgrest);
        var bookRepository = new SupabaseBookRepository(postgrest);
        var userProfileRepository = new SupabaseUserProfileRepository(postgrest);
        var coverStorage = new SupabaseBookCoverImageStorage(storageApi);

        // Auth
        this.authClient = new SupabaseAuthClient(supabase);

        // Use cases
        this.getBooksUseCase = new GetBooksUseCase(bookQueryRepository);
        this.findAllCategoriesUseCase = new FindAllCategoriesUseCase(new SupabaseCategoryQueryRepository(postgrest));

        this.getBookByIdUseCase = new GetBookByIdUseCase(bookRepository);
        this.createBookUseCase = new CreateBookUseCase(bookRepository, coverStorage);
        this.updateBookUseCase = new UpdateBookUseCase(bookRepository, coverStorage);
        this.deleteBookUseCase = new DeleteBookUseCase(bookRepository, coverStorage);

        this.findAllAuthorsUseCase = new FindAllAuthorsUseCase(new SupabaseAuthorQueryRepository(postgrest));
        this.getCurrentUserUseCase = new GetCurrentUserUseCase(authClient, userProfileRepository);
    }

    public static AppContext get() {
        if (INSTANCE == null) throw new IllegalStateException("AppContext not initialized");
        return INSTANCE;
    }

    public CreateCategoryUseCase getCreateCategoryUseCase() {
        return new CreateCategoryUseCase(new SupabaseCategoryQueryRepository(postgrest));
    }

    public DeleteCategoryUseCase getDeleteCategoryUseCase() {
        return new DeleteCategoryUseCase(new SupabaseCategoryQueryRepository(postgrest));
    }

    public FindCategoryByIdUseCase getFindCategoryByIdUseCase() {
        return new FindCategoryByIdUseCase(new SupabaseCategoryQueryRepository(postgrest));
    }

    public static void init() {
        INSTANCE = new AppContext();
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
        CoverImageUrlGenerator coverResolver = new SupabaseCoverImageUrlGenerator(storageApi, this.signedUrlTimeinSeconds);

        replaceSession(new SessionContext(user, coverResolver));
        return s;
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------
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

    public SessionContext session() {
        if (session == null) throw new IllegalStateException("No active session (user not logged in)");
        return session;
    }
}
