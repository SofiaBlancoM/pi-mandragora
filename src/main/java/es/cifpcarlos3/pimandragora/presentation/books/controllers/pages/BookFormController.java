package es.cifpcarlos3.pimandragora.presentation.books.controllers.pages;

import es.cifpcarlos3.pimandragora.application.authors.usecases.findallauthors.dtos.FindAllAuthorsResponse;
import es.cifpcarlos3.pimandragora.application.books.usecases.getbyid.dtos.GetBookByIdResponse;
import es.cifpcarlos3.pimandragora.application.categories.usecases.findallcategories.dtos.FindAllCategoriesResponse;
import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;
import es.cifpcarlos3.pimandragora.shared.utils.FilePickerDefaults;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class BookFormController {

    @FXML
    private Button deleteButton;
    @FXML
    private Label coverHint;
    @FXML
    private VBox root;
    @FXML
    private Label titleHeader;
    @FXML
    private Label statusChip;
    @FXML
    private Label coverBadge;
    @FXML
    private ImageView coverPreview;
    @FXML
    private Button changeCoverButton;
    @FXML
    private Button removeCoverButton;
    @FXML
    private TextField titleField;
    @FXML
    private TextField isbnField;
    @FXML
    private ComboBox<FindAllAuthorsResponse> authorCombo;
    @FXML
    private TextField publisherField;
    @FXML
    private DatePicker publicationDatePicker;
    @FXML
    private ComboBox<BookStatus> statusCombo;
    @FXML
    private TextField priceField;
    @FXML
    private Spinner<Integer> stockSpinner;
    @FXML
    private Label titleError;
    @FXML
    private Label isbnError;
    @FXML
    private Label authorError;
    @FXML
    private Label categoryError;
    @FXML
    private Label priceError;
    @FXML
    private Label stockError;
    @FXML
    private Label formError;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private ComboBox<FindAllCategoriesResponse> categoryCombo;
    private Runnable onDelete = () -> {
    };
    private boolean removeCoverRequested = false;
    private Image placeholderCover;
    private Mode mode = Mode.CREATE;
    private UUID editingBookId;
    private String existingCoverPath;
    private java.nio.file.Path newCoverFile;
    private Runnable onSave = () -> {
    };
    private Runnable onCancel = () -> {
    };
    private UUID pendingAuthorId;
    private UUID pendingCategoryId;

    public BookFormDraft getDraft() {
        FindAllAuthorsResponse selectedAuthor = authorCombo.getValue();
        FindAllCategoriesResponse selectedCategory = categoryCombo.getValue();

        return new BookFormDraft(
                editingBookId,
                trim(titleField.getText()),
                trim(isbnField.getText()),
                selectedAuthor == null ? null : selectedAuthor.id(),
                selectedCategory == null ? null : selectedCategory.id(),
                trim(publisherField.getText()),
                publicationDatePicker.getValue(),
                parsePrice(priceField.getText()),
                stockSpinner.getValue(),
                statusCombo.getValue(),
                existingCoverPath,
                newCoverFile,
                removeCoverRequested
        );
    }

    private static String trim(String string) {
        if (string == null) return null;
        String x = string.trim();
        return x.isBlank() ? null : x;
    }

    private static BigDecimal parsePrice(String raw) {
        if (raw == null) return null;
        String price = raw.trim();
        if (price.isBlank()) return null;
        price = price.replace(',', '.');
        return new BigDecimal(price);
    }

    public void setAuthors(List<FindAllAuthorsResponse> authors) {
        authorCombo.getItems().setAll(authors);
        selectAuthorById(pendingAuthorId);
    }

    private void selectAuthorById(UUID authorId) {
        if (authorId == null) {
            authorCombo.getSelectionModel().clearSelection();
            return;
        }

        for (FindAllAuthorsResponse authorsResponse : authorCombo.getItems()) {
            if (authorId.equals(authorsResponse.id())) {
                authorCombo.getSelectionModel().select(authorsResponse);
                return;
            }
        }

        authorCombo.getSelectionModel().clearSelection();
    }

    public void setCategories(List<FindAllCategoriesResponse> categories) {
        categoryCombo.getItems().setAll(categories);
        selectCategoryById(pendingCategoryId);
    }

    private void selectCategoryById(UUID categoryId) {
        if (categoryId == null) {
            categoryCombo.getSelectionModel().clearSelection();
            return;
        }

        for (FindAllCategoriesResponse categoriesResponse : categoryCombo.getItems()) {
            if (categoryId.equals(categoriesResponse.id())) {
                categoryCombo.getSelectionModel().select(categoriesResponse);
                return;
            }
        }

        categoryCombo.getSelectionModel().clearSelection();
    }

    public void setCreateMode() {
        this.mode = Mode.CREATE;
        this.editingBookId = null;
        this.existingCoverPath = null;
        this.newCoverFile = null;

        titleHeader.setText("Nuevo libro");
        saveButton.setText("Crear");

        statusChip.setVisible(false);
        statusChip.setManaged(false);

        coverBadge.setText("");
        coverBadge.setVisible(false);
        coverBadge.setManaged(false);

        removeCoverRequested = false;

        deleteButton.setVisible(false);
        deleteButton.setManaged(false);

        clearForm();
        clearErrors();
    }

    private void clearForm() {
        titleField.clear();
        isbnField.clear();

        authorCombo.getSelectionModel().clearSelection();
        categoryCombo.getSelectionModel().clearSelection();

        publisherField.clear();
        publicationDatePicker.setValue(null);
        statusCombo.getSelectionModel().select(BookStatus.ACTIVE);

        priceField.clear();
        stockSpinner.getValueFactory().setValue(0);

        coverPreview.setImage(placeholderCover);
    }

    private void clearErrors() {
        hide(titleError);
        hide(isbnError);
        hide(authorError);
        hide(categoryError);
        hide(priceError);
        hide(stockError);

        formError.setText("");
        hide(formError);
    }

    private static void hide(Label label) {
        label.setVisible(false);
        label.setManaged(false);
    }

    public void setEditMode(GetBookByIdResponse book) {
        this.mode = Mode.EDIT;
        this.editingBookId = book.id();
        this.existingCoverPath = book.coverImagePath();
        this.newCoverFile = null;

        this.pendingAuthorId = book.authorId();
        this.pendingCategoryId = book.categoryId();

        titleHeader.setText("Actualizar libro");
        saveButton.setText("Guardar cambios");

        fillFrom(book);
        coverBadge.setText("");
        coverBadge.setVisible(false);
        coverBadge.setManaged(false);

        removeCoverRequested = false;
        clearErrors();

        statusChip.setText(label(book.status()));
        statusChip.setVisible(true);
        statusChip.setManaged(true);

        deleteButton.setVisible(true);
        deleteButton.setManaged(true);

    }

    private void fillFrom(GetBookByIdResponse book) {
        titleField.setText(book.title());
        isbnField.setText(book.isbn());

        publisherField.setText(book.publisher());
        publicationDatePicker.setValue(book.publicationDate());

        statusCombo.getSelectionModel().select(book.status());

        priceField.setText(book.price() != null ? book.price().toPlainString() : "");
        stockSpinner.getValueFactory().setValue(book.stock());

        selectAuthorById(book.authorId());
        selectCategoryById(book.categoryId());
    }

    private static String label(BookStatus s) {
        if (s == null) return "";
        return switch (s) {
            case ACTIVE -> "Activo";
            case OUT_OF_STOCK -> "Sin stock";
            case DISCONTINUED -> "Descatalogado";
        };
    }

    public void setOnCancel(Runnable runnable) {
        this.onCancel = (runnable == null) ? () -> {
        } : runnable;
    }

    public void setOnDelete(Runnable runnable) {
        this.onDelete = (runnable == null) ? () -> {
        } : runnable;
    }

    public void setOnSave(Runnable runnable) {
        this.onSave = (runnable == null) ? () -> {
        } : runnable;
    }

    public enum Mode {CREATE, EDIT}

    public record BookFormDraft(
            UUID id,
            String title,
            String isbn,
            UUID authorId,
            UUID categoryId,
            String publisher,
            LocalDate publicationDate,
            BigDecimal price,
            Integer stock,
            BookStatus status,
            String existingCoverPath,
            java.nio.file.Path newCoverFile,
            boolean removeCover
    ) {
    }

    @FXML
    private void initialize() {
        placeholderCover = coverPreview.getImage();

        statusCombo.getItems().setAll(
                Arrays.stream(BookStatus.values())
                        .sorted(Comparator.comparing(BookStatus::getDisplayName))
                        .toList()
        );
        statusCombo.getSelectionModel().select(BookStatus.ACTIVE);

        statusCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(BookStatus status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? null : status.getDisplayName());
            }
        });

        statusCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(BookStatus status, boolean empty) {
                super.updateItem(status, empty);
                setText(empty || status == null ? null : status.getDisplayName());
            }
        });

        stockSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 0));
        stockSpinner.setEditable(true);

        statusChip.setVisible(false);
        statusChip.setManaged(false);

        coverBadge.setVisible(false);
        coverBadge.setManaged(false);

        changeCoverButton.setOnAction(event -> pickNewCoverFile());

        removeCoverButton.setOnAction(event -> {
            newCoverFile = null;
            removeCoverRequested = true;
            existingCoverPath = null;

            coverPreview.setImage(placeholderCover);

            coverBadge.setText("Portada eliminada");
            coverBadge.setVisible(true);
            coverBadge.setManaged(true);
        });


        authorCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(FindAllAuthorsResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.fullName());
            }
        });

        authorCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FindAllAuthorsResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.fullName());
            }
        });

        categoryCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(FindAllCategoriesResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
            }
        });
        categoryCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(FindAllCategoriesResponse item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.name());
            }
        });

        saveButton.setOnAction(e -> onSave.run());
        cancelButton.setOnAction(e -> onCancel.run());
        deleteButton.setOnAction(e -> onDelete.run());

    }

    private void pickNewCoverFile() {
        Window owner = root.getScene() != null ? root.getScene().getWindow() : null;

        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar imagen de portada");
        fc.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        File downloads = FilePickerDefaults.defaultInitialDirectory();
        if (downloads != null) {
            fc.setInitialDirectory(downloads);
        }
        File selected = fc.showOpenDialog(owner);
        if (selected == null) return;

        Path file = selected.toPath();

        this.newCoverFile = file;
        removeCoverRequested = false;

        setCoverPreview(new Image(file.toUri().toString(), true));

        coverBadge.setText("Nueva portada");
        coverBadge.setVisible(true);
        coverBadge.setManaged(true);
    }

    public void setCoverPreview(Image image) {
        coverPreview.setImage(image == null ? placeholderCover : image);
    }

}
