package es.cifpcarlos3.pimandragora.presentation.books.controllers.pages;

import es.cifpcarlos3.pimandragora.application.authors.usecases.dtos.FindAllAuthorsResponse;
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

    private static String trim(String s) {
        if (s == null) return null;
        String x = s.trim();
        return x.isBlank() ? null : x;
    }

    private static BigDecimal parsePrice(String raw) {
        if (raw == null) return null;
        String x = raw.trim();
        if (x.isBlank()) return null;
        x = x.replace(',', '.');
        return new BigDecimal(x);
    }
    // -----------------------------
    // Public API for page controllers
    // -----------------------------

    public void setAuthors(List<FindAllAuthorsResponse> authors) {
        authorCombo.getItems().setAll(authors);
        selectAuthorById(pendingAuthorId);
    }

    private void selectAuthorById(UUID authorId) {
        if (authorId == null) {
            authorCombo.getSelectionModel().clearSelection();
            return;
        }

        for (FindAllAuthorsResponse a : authorCombo.getItems()) {
            if (authorId.equals(a.id())) {
                authorCombo.getSelectionModel().select(a);
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

        for (FindAllCategoriesResponse c : categoryCombo.getItems()) {
            if (categoryId.equals(c.id())) {
                categoryCombo.getSelectionModel().select(c);
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

        titleHeader.setText("New book");
        saveButton.setText("Create");

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

    // -----------------------------
    // Internal helpers
    // -----------------------------

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

    private static void hide(Label l) {
        l.setVisible(false);
        l.setManaged(false);
    }

    public void setEditMode(GetBookByIdResponse book) {
        this.mode = Mode.EDIT;
        this.editingBookId = book.id();
        this.existingCoverPath = book.coverImagePath();
        this.newCoverFile = null;

        // store for later in case lists are still empty
        this.pendingAuthorId = book.authorId();
        this.pendingCategoryId = book.categoryId();

        titleHeader.setText("Book details");
        saveButton.setText("Save changes");

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

    public void setOnCancel(Runnable r) {
        this.onCancel = (r == null) ? () -> {
        } : r;
    }

    public void setOnDelete(Runnable r) {
        this.onDelete = (r == null) ? () -> {
        } : r;
    }

    public void setOnSave(Runnable r) {
        this.onSave = (r == null) ? () -> {
        } : r;
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

        changeCoverButton.setOnAction(e -> pickNewCoverFile());

        removeCoverButton.setOnAction(e -> {
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
        fc.setTitle("Select cover image");
        fc.getExtensionFilters().setAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );

        // ✅ Start in Downloads (best-effort cross-platform)
        File downloads = FilePickerDefaults.defaultInitialDirectory();
        if (downloads != null) {
            fc.setInitialDirectory(downloads);
        }
        File selected = fc.showOpenDialog(owner);
        if (selected == null) return;

        Path file = selected.toPath();

        this.newCoverFile = file;
        removeCoverRequested = false;

        // Preview immediately (local file)
        setCoverPreview(new Image(file.toUri().toString(), true));

        coverBadge.setText("Nueva portada");
        coverBadge.setVisible(true);
        coverBadge.setManaged(true);
    }


    public void setCoverPreview(Image image) {
        coverPreview.setImage(image == null ? placeholderCover : image);
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

}
