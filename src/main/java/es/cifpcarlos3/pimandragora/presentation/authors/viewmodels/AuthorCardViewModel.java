package es.cifpcarlos3.pimandragora.presentation.authors.viewmodels;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AuthorCardViewModel {
    private final UUID id;
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty biography = new SimpleStringProperty();

    public AuthorCardViewModel(UUID id, String fullName, String biography) {
        this.id = id;
        this.fullName.set(fullName);
        this.biography.set(biography != null ? biography : "Sin biografía disponible.");
    }
}