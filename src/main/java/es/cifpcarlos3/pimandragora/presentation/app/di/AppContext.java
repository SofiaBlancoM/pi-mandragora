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
import es.cifpcarlos3.pimandragora.application.categories.usecases.createcategory.CreateCategoryUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.deletecategory.DeleteCategoryUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.FindAllCategoriesUseCase;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findcategorybyid.FindCategoryByIdUseCase;
import es.cifpcarlos3.pimandragora.application.common.images.CoverImageUrlGenerator;
import es.cifpcarlos3.pimandragora.application.userprofile.usecases.getcurrentuser.GetCurrentUserUseCase;
import es.cifpcarlos3.pimandragora.infrastructure.auth.SupabaseAuthClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors.SupabaseAuthorCommandRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.authors.SupabaseAuthorQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books.SupabaseBookQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.books.SupabaseBookRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.categories.SupabaseCategoryQueryRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.repositories.users.SupabaseUserProfileRepository;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.PostgreClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.StorageApi;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseHttpClient;
import es.cifpcarlos3.pimandragora.infrastructure.data.supabase.SupabaseSession;
import es.cifpcarlos3.pimandragora.infrastructure.external.WikipediaClient;
import es.cifpcarlos3.pimandragora.infrastructure.images.SupabaseBookCoverImageStorage;
import es.cifpcarlos3.pimandragora.infrastructure.images.SupabaseCoverImageUrlGenerator;
import es.cifpcarlos3.pimandragora.presentation.app.config.AppConfig;
import es.cifpcarlos3.pimandragora.presentation.app.config.PropertyKey;
import es.cifpcarlos3.pimandragora.presentation.app.constants.ConfigConstants;
import es.cifpcarlos3.pimandragora.presentation.authors.viewmodels.AuthorsViewModel;
import es.cifpcarlos3.pimandragora.presentation.books.viewmodels.BooksViewModel;
import lombok.AccessLevel;
import lombok.Getter;

public final class AppContext {

    private static volatile AppContext INSTANCE;

    // Infrastructure
    @Getter(AccessLevel.NONE)
    private final SupabaseHttpClient supabase;
    @Getter(AccessLevel.NONE)
    private final PostgreClient postgrest;
    @Getter(AccessLevel.NONE)
    private final StorageApi storageApi;

    // ESTO ES LO QUE TE FALTABA DECLARAR (Captura 4)
    private final WikipediaClient wikipediaClient;
    private final SupabaseAuthorCommandRepository authorCommandRepository;

    // Application casos de uso
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

    // Configuración
    int pageSize = AppConfig.getInt(PropertyKey.BOOKS_PAGE_SIZE, ConfigConstants.DEFAULT_BOOKS_PAGE_SIZE);
    int signedUrlTimeinSeconds = AppConfig.getInt(PropertyKey.SIGNED_URL_TTL_SECONDS, ConfigConstants.DEFAULT_SIGNED_URL_TTL);

    private volatile SessionContext session;

    private AppContext() {
        this.supabase = new SupabaseHttpClient();
        this.postgrest = new PostgreClient(supabase);
        this.storageApi = new StorageApi(supabase);

        // Inicializamos los campos nuevos
        this.wikipediaClient = new WikipediaClient();
        this.authorCommandRepository = new SupabaseAuthorCommandRepository(postgrest);

        var bookQueryRepository = new SupabaseBookQueryRepository(postgrest);
        var bookRepository = new SupabaseBookRepository(postgrest);
        var userProfileRepository = new SupabaseUserProfileRepository(postgrest);
        var coverStorage = new SupabaseBookCoverImageStorage(storageApi);
        var authorQueryRepository = new SupabaseAuthorQueryRepository(postgrest);

        this.authClient = new SupabaseAuthClient(supabase);
        this.getBooksUseCase = new GetBooksUseCase(bookQueryRepository);
        this.findAllCategoriesUseCase = new FindAllCategoriesUseCase(new SupabaseCategoryQueryRepository(postgrest));

        this.getBookByIdUseCase = new GetBookByIdUseCase(bookRepository);
        this.createBookUseCase = new CreateBookUseCase(bookRepository, coverStorage);
        this.updateBookUseCase = new UpdateBookUseCase(bookRepository, coverStorage);
        this.deleteBookUseCase = new DeleteBookUseCase(bookRepository, coverStorage);

        // Ahora coinciden los 3 argumentos con los 3 campos
        this.findAllAuthorsUseCase = new FindAllAuthorsUseCase(authorQueryRepository, authorCommandRepository, wikipediaClient);
        this.getCurrentUserUseCase = new GetCurrentUserUseCase(authClient, userProfileRepository);
    }

    public static AppContext get() {
        if (INSTANCE == null) throw new IllegalStateException("AppContext no inicializado");
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

    public boolean isLoggedIn() {
        return session != null && SupabaseSession.hasToken();
    }

    public AuthSessionDto login(String email, String password) {
        AuthSessionDto authSessionDto = authClient.login(email, password);
        AuthUserDto user = authClient.getCurrentUser();

        // CORRECCIÓN: Usamos Generator (el nombre que tienes en tus archivos)
        CoverImageUrlGenerator coverResolver = new SupabaseCoverImageUrlGenerator(storageApi, this.signedUrlTimeinSeconds);

        replaceSession(new SessionContext(user, coverResolver));
        return authSessionDto;
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

    public BooksViewModel newBooksViewModel() {
        return new BooksViewModel(getBooksUseCase, session().coverResolver(), this.pageSize);
    }
    public AuthorsViewModel newAuthorsViewModel() {
        return new AuthorsViewModel(findAllAuthorsUseCase);
    }

    public SessionContext session() {
        if (session == null) throw new IllegalStateException("Usuario no logueado");
        return session;
    }
    public CreateAuthorUseCase getCreateAuthorUseCase() {
        return new CreateAuthorUseCase(authorCommandRepository);
    }

    public SupabaseAuthorCommandRepository getAuthorCommandRepository() {
        return this.authorCommandRepository;
    }
}