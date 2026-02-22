package es.cifpcarlos3.pimandragora.presentation.authors.viewmodels;

import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.AuthorDetailResponse;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.DeleteAuthorUseCase;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.GetAuthorDetailUseCase;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.UpdateAuthorUseCase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;

public class AuthorDetailViewModel {
    private final GetAuthorDetailUseCase getAuthorDetailUseCase;
    private final UpdateAuthorUseCase updateAuthorUseCase;
    private final DeleteAuthorUseCase deleteAuthorUseCase;

    // Propiedades para la Vista (Binding)
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty bio = new SimpleStringProperty();
    private final ObservableList<AuthorDetailResponse.AuthorBookResponse> books = FXCollections.observableArrayList();
    private final BooleanProperty loading = new SimpleBooleanProperty(false);

    private String currentId;

    public AuthorDetailViewModel(
            GetAuthorDetailUseCase getAuthorDetailUseCase,
            UpdateAuthorUseCase updateAuthorUseCase,
            DeleteAuthorUseCase deleteAuthorUseCase
    ) {
        this.getAuthorDetailUseCase = getAuthorDetailUseCase;
        this.updateAuthorUseCase = updateAuthorUseCase;
        this.deleteAuthorUseCase = deleteAuthorUseCase;
    }

    public void loadAuthor(String id) {
        this.currentId = id;
        loading.set(true);

        AuthorDetailResponse response = getAuthorDetailUseCase.execute(id);

        if (response != null) {
            fullName.set(response.fullName());
            bio.set(response.bio());
            books.setAll(response.books());
        }
        loading.set(false);
    }

    public void updateAuthor() {
        if (currentId != null) {

            updateAuthorUseCase.execute(currentId, Map.of(
                    "full_name", fullName.get(),
                    "bio", bio.get()
            ));
            System.out.println("Cambios guardados para: " + currentId);
        }
    }


    public StringProperty fullNameProperty() { return fullName; }
    public StringProperty bioProperty() { return bio; }
    public ObservableList<AuthorDetailResponse.AuthorBookResponse> getBooks() { return books; }
    public BooleanProperty loadingProperty() { return loading; }


}