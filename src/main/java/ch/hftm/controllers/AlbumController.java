package ch.hftm.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import ch.hftm.service.ImageService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;


public class AlbumController {

    private static final int ATTR_AMOUNT = 3; // Used to test dynamic attribute and value adding to imageGrid
    private Album album;
    private List<Image> imagesToDelete = new ArrayList<>();

    @FXML
    private Label albumTitle;

    @FXML
    private Label albumStatus;

    @FXML
    private GridPane imageGrid;

    @FXML
    private Pane imageContainer;

    public void setAlbum(Album album) {
        this.album = album;
        setupAlbumPage();
    }

    private void setupAlbumPage() {
        if (album == null) {
            return;
        }
        albumTitle.setText("Album: " + album.getName());
        initializeAlbum();
    }

    /**
     * Create a pane to display an image including the meta attribut descriptors and values
     */
    private Pane createImagePane(Image image, Pane samplePane) {
        Pane imagePane = new Pane();
        imagePane.setPrefHeight(samplePane.getPrefHeight());
        imagePane.setPrefWidth(samplePane.getPrefWidth());
        imagePane.setOnMouseClicked(event -> {
                ImageService imageService = new ImageService();
                Node source = (Node) event.getSource();
                Label imageName = (Label) source.lookup("#imageName");
                Image image2 = imageService.getImageByFileName(imageName.getText());
                if (source.getStyle().equals("")) {
                    source.setStyle("-fx-border-style: solid; -fx-border-width: 3; -fx-border-color: blue;"); 
                    imagesToDelete.add(image2);
                } else {
                    source.setStyle(null);
                    imagesToDelete.remove(image2);
                } 
        });

        Label imageName = new Label(image.getFileName());
        imageName.setId("imageName");
        imageName.setVisible(false);

        ImageView sampleImageView = (ImageView) samplePane.lookup("#imageView");
        ImageView imageView = new ImageView();
        imageView.setFitHeight(sampleImageView.getFitHeight());
        imageView.setFitWidth(sampleImageView.getFitWidth());
        javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new File(image.getFullPath()).toURI().toString());
        imageView.setImage(fxImage);

        GridPane imageMetaPane = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35.0);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65.0);
        imageMetaPane.getColumnConstraints().add(col1);
        imageMetaPane.getColumnConstraints().add(col1);

        // Add an amount of descriptor labels to the grid based on the amount of the most important meta values
        createLabel(imageMetaPane, 0, "Attribute");

        // Add an amount of value labels to the grid based on the amount of the most important meta value
        createLabel(imageMetaPane, 1, "Value");

        BorderPane imageBorderPane = new BorderPane();
        imageBorderPane.setTop(imageView);
        BorderPane.setMargin(imageView, new Insets(5, 5, 0, 5));
        imageBorderPane.setCenter(imageMetaPane);
        BorderPane.setMargin(imageMetaPane, new Insets(10, 10, 0, 10));

        imagePane.getChildren().add(imageBorderPane);
        imagePane.getChildren().add(imageName);
        return imagePane;
    }

    private void createLabel(GridPane pane, int column, String labelText) {
        for (int i = 0; i < ATTR_AMOUNT; i++) {
            Label label = new Label();
            label.setText(String.format("%s %d:",labelText, i));
            label.setVisible(true);
            pane.add(label, column, i);
        }
    }

    @FXML
    public void deleteImage() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete selected images");
        alert.setContentText("If you want to delete the selected images press OK.");
        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(okButton, cancelButton);
        Optional<ButtonType> choice = alert.showAndWait();

        if (choice.isPresent() && choice.get() == okButton) {
            ImageService imageService = new ImageService();
            if (!(imagesToDelete.isEmpty())) {
                int i = 0;
                for (Image image : imagesToDelete) {
                    imageService.deleteImage(image);
                    i++;
                }
                updateAlbumStatus(String.format("Deleted %d image(s).", i), 5_000);
                initializeAlbum();
            } else {
                Alert info = new Alert(AlertType.INFORMATION);
                info.setTitle("Invalid selection");
                info.setContentText("No images where selected.");
                info.showAndWait();
            }
        } else {
            for (Node child : imageGrid.getChildren()) {
                child.setStyle(null);
            }
        }
        imagesToDelete.clear();
    }

    /**
     * 
     * @param status The status to be shown in the album view
     * @param duration The status is shown for this duration
     */
    private void updateAlbumStatus(String status, double duration) {
        albumStatus.setVisible(true);
        albumStatus.setText(status);
        Timeline timeline = new Timeline(
            new KeyFrame(
                Duration.millis(duration), 
                kf -> albumStatus.setText("")));
        timeline.play();
        timeline.setOnFinished(e -> albumStatus.setVisible(false));
    }

    public void initializeAlbum() {
        // Clear the imageGrid of all children nodes
        imageGrid.getChildren().clear();
        
        ImageService imageService = new ImageService();
        List<Image> images = imageService.getImagesForAlbum(album);

        int col = 0;
        int row = 0;
        // Create an pane for each image in the album
        for (Image image : images) {
            imageGrid.add(createImagePane(image, imageContainer), col++, row);
            // Place the image on a new row if the current column is full of images
            if (col % imageGrid.getColumnCount() == 0) {
                row++;
                col = 0;
            }
        }

    }

}
