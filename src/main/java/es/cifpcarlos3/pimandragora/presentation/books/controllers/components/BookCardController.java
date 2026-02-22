package es.cifpcarlos3.pimandragora.presentation.books.controllers.components;

import es.cifpcarlos3.pimandragora.domain.enums.BookStatus;
import es.cifpcarlos3.pimandragora.presentation.books.viewmodels.BookCardListViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class BookCardController {

    private static final ColorAdjust GRAYSCALE = new ColorAdjust(0, -1, 0, 0);

    @FXML
    private VBox root;
    @FXML
    private ImageView coverImageView;
    @FXML
    private Label titleLabel;
    @FXML
    private Label authorLabel;
    @FXML
    private Label categoryLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label statusBadge;
    @FXML
    private Label priceLabel;

    private Image placeholder;

    private Runnable onClick = () -> {
    };

    public void bind(BookCardListViewModel viewModel) {
        unbind();

        titleLabel.textProperty().bind(viewModel.getTitle());
        authorLabel.textProperty().bind(viewModel.getAuthorFullName());
        categoryLabel.textProperty().bind(viewModel.getCategoryName());

        yearLabel.textProperty().bind(
                Bindings.when(viewModel.getYear().greaterThan(0))
                        .then(viewModel.getYear().asString("Publicado · %d"))
                        .otherwise("")
        );
        yearLabel.visibleProperty().bind(yearLabel.textProperty().isNotEmpty());
        yearLabel.managedProperty().bind(yearLabel.visibleProperty());

        priceLabel.textProperty().bind(viewModel.getPriceText());
        priceLabel.setVisible(true);
        priceLabel.setManaged(true);

        viewModel.getCoverImageUrl().addListener((obs, oldV, newV) -> loadCover(newV));
        loadCover(viewModel.getCoverImageUrl().get());

        viewModel.getStatus().addListener((obs, oldV, newV) -> applyStatus(newV));
        applyStatus(viewModel.getStatus().get());
    }

    private void applyStatus(BookStatus status) {
        root.getStyleClass().removeAll("status-active", "status-outofstock", "status-discontinued");

        if (status == null) status = BookStatus.ACTIVE;

        coverImageView.setEffect(null);

        switch (status) {
            case ACTIVE -> {
                root.getStyleClass().add("status-active");
                statusBadge.setVisible(false);
                statusBadge.setManaged(false);
            }
            case OUT_OF_STOCK -> {
                root.getStyleClass().add("status-outofstock");
                statusBadge.setText("Sin Stock");
                statusBadge.setVisible(true);
                statusBadge.setManaged(true);
            }
            case DISCONTINUED -> {
                root.getStyleClass().add("status-discontinued");
                statusBadge.setText("Descatalogado");
                coverImageView.setEffect(GRAYSCALE);
                statusBadge.setVisible(true);
                statusBadge.setManaged(true);
            }
        }
    }

    private void unbind() {
        titleLabel.textProperty().unbind();
        authorLabel.textProperty().unbind();
        categoryLabel.textProperty().unbind();
        yearLabel.textProperty().unbind();
        priceLabel.textProperty().unbind();
    }

    private void loadCover(String url) {
        if (url == null || url.isBlank()) {
            coverImageView.setImage(placeholder);
            return;
        }

        boolean looksLikeHttp = url.startsWith("http://") || url.startsWith("https://");
        boolean looksLikeFile = url.startsWith("file:");

        if (!looksLikeHttp && !looksLikeFile) {
            coverImageView.setImage(placeholder);
            return;
        }

        try {
            Image image = new Image(url, true);
            image.errorProperty().addListener((obs, wasError, isError) -> {
                if (isError) coverImageView.setImage(placeholder);
            });
            image.exceptionProperty().addListener((obs, oldEx, ex) -> {
                if (ex != null) coverImageView.setImage(placeholder);
            });

            coverImageView.setImage(image);
        } catch (IllegalArgumentException ex) {
            coverImageView.setImage(placeholder);
        }
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = (onClick == null) ? () -> {
        } : onClick;
    }

    @FXML
    private void initialize() {
        placeholder = coverImageView.getImage();

        root.setOnMouseClicked(event -> {
            event.consume();
            onClick.run();
        });
    }
}
