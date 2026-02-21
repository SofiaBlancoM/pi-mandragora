package es.cifpcarlos3.pimandragora.domain.enums;

import lombok.Getter;

@Getter
public enum BookStatus {

    ACTIVE("Activo"),
    OUT_OF_STOCK("Sin stock"),
    DISCONTINUED("Descatalogado");

    private final String displayName;

    BookStatus(String displayName) {
        this.displayName = displayName;
    }

}
