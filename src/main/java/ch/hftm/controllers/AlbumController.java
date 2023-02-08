package ch.hftm.controllers;

import java.io.File;
import java.util.List;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import ch.hftm.service.ImageService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;


public class AlbumController {

    private static final int ATTR_AMOUNT = 3; // Used to test dynamic attribute and value adding to imageGrid
    private Album album;
    private ImageService imageService;
    private List<Image> images;

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
        // TODO: Get all images of album and display them nicely in a grid
        initializeAlbum();
    }

    /**
     * Create a pane to display an image including the meta attribut descriptors and values
     */
    private Pane createImagePane(Image image, Pane samplePane) {
        Pane imagePane = new Pane();
        imagePane.setPrefHeight(samplePane.getPrefHeight());
        imagePane.setPrefWidth(samplePane.getPrefWidth());

        ImageView sampleImageView = (ImageView) samplePane.lookup("#imageView");
        ImageView imageView = new ImageView();
        imageView.setFitHeight(sampleImageView.getFitHeight());
        imageView.setFitWidth(sampleImageView.getFitWidth());
        javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new File(image.getFullPath()).toURI().toString());
        imageView.setImage(fxImage);

        GridPane sampleMetaPane = (GridPane) samplePane.lookup("#imageMetaGrid");
        GridPane imageMetaPane = new GridPane();
        imageMetaPane.setAlignment(sampleMetaPane.getAlignment());
        imageMetaPane.setLayoutX(sampleMetaPane.getLayoutX());
        imageMetaPane.setLayoutY(sampleMetaPane.getLayoutY());
        imageMetaPane.setPrefHeight(sampleMetaPane.getPrefHeight());
        imageMetaPane.setPrefWidth(sampleMetaPane.getPrefWidth());
        // TODO: Fix column width of imageMetaPane

        // Add an amount of descriptor labels to the grid based on the amount of the most important meta values
        for (int i = 0; i < ATTR_AMOUNT; i++) {
            Label imageAttr = new Label();
            imageAttr.setText(String.format("Attribute %d:", i));
            imageAttr.setVisible(true);
            imageMetaPane.add(imageAttr, 0, i);
        }

        // Add an amount of value labels to the grid based on the amount of the most important meta value
        for (int i = 0; i < ATTR_AMOUNT; i++) {
            Label imageValue = new Label();
            imageValue.setText(String.format("Value %d:", i));
            imageValue.setVisible(true);
            imageMetaPane.add(imageValue, 1, i);
        }

        BorderPane imageBorderPane = new BorderPane();
        imageBorderPane.setTop(imageView);
        BorderPane.setMargin(imageView, new Insets(5, 5, 0, 5));
        imageBorderPane.setCenter(imageMetaPane);
        BorderPane.setMargin(imageMetaPane, new Insets(0, 10, 0, 10));

        imagePane.getChildren().add(imageBorderPane);
        return imagePane;
    }

    public void initializeAlbum() {
        System.out.println(album.getId());
        imageService = new ImageService();
        images = imageService.getImagesForAlbum(album);

        int col = 0;
        int row = 0;
        // Create an pane for each image in the album
        for (Image image : images) {
            imageGrid.add(createImagePane(image, imageContainer), col++, row);
            // Place the image on a new row if the current column is full of images
            if (col % imageGrid.getColumnCount() == 0) {
                row++;
            }
        }

    }

}
