package es.cifpcarlos3.pimandragora.presentation.authors.viewmodels;

import es.cifpcarlos3.pimandragora.application.authors.usecases.FindAllAuthorsUseCase;
import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.FindAllAuthorsResponse;
import es.cifpcarlos3.pimandragora.shared.paging.Page;
import es.cifpcarlos3.pimandragora.shared.paging.PageRequest;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class AuthorsViewModel {

    private final FindAllAuthorsUseCase findAllAuthorsUseCase;
    private final StringProperty error = new SimpleStringProperty(null);

    private final StringProperty searchText = new SimpleStringProperty("");
    private final StringProperty sortOrder = new SimpleStringProperty("full_name.asc");
    private final BooleanProperty loading = new SimpleBooleanProperty(false);
    private final IntegerProperty totalItems = new SimpleIntegerProperty(0);
    private final IntegerProperty totalPages = new SimpleIntegerProperty(0);

    private final ObservableList<AuthorCardViewModel> items = FXCollections.observableArrayList();

    public AuthorsViewModel(FindAllAuthorsUseCase findAllAuthorsUseCase) {
        this.findAllAuthorsUseCase = findAllAuthorsUseCase;
    }

    public void loadData(int pageIndex) {
        loading.set(true);
        error.set(null);


        String filtro = searchText.get().toLowerCase();
        String orden = sortOrder.get();

        new Thread(() -> {
            try {

                PageRequest request = new PageRequest(pageIndex, 9);
                Page<FindAllAuthorsResponse> page = findAllAuthorsUseCase.execute(request);

                Platform.runLater(() -> {

                    List<AuthorCardViewModel> cardViewModels = page.items().stream()
                            .filter(a -> a.fullName().toLowerCase().contains(filtro))
                            .sorted((a1, a2) -> {

                                if (orden.contains("desc")) {
                                    return a2.fullName().compareToIgnoreCase(a1.fullName());
                                }
                                return a1.fullName().compareToIgnoreCase(a2.fullName());
                            })
                            .map(a -> new AuthorCardViewModel(a.id(), a.fullName(), a.bio()))
                            .toList();

                    items.setAll(cardViewModels);
                    totalItems.set((int) page.totalItems());
                    totalPages.set((int) page.totalPages());
                    loading.set(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    error.set("Error: " + e.getMessage());
                    loading.set(false);
                });
            }
        }).start();
    }

    public StringProperty searchTextProperty() { return searchText; }
    public StringProperty sortOrderProperty() { return sortOrder; }
    public BooleanProperty loadingProperty() { return loading; }
    public IntegerProperty totalItemsProperty() { return totalItems; }
    public IntegerProperty totalPagesProperty() { return totalPages; }
    public ObservableList<AuthorCardViewModel> getItems() { return items; }
    public StringProperty errorProperty() { return error; }
}