package ch.hftm.controllers;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import ch.hftm.service.ImageService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;


public class AlbumController {

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
    }

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
        imageMetaPane.setPrefHeight(sampleMetaPane.getPrefHeight());
        imageMetaPane.setPrefWidth(sampleMetaPane.getPrefWidth());

        imagePane.getChildren().add(imageView);
        imagePane.getChildren().add(imageMetaPane);
        return imagePane;
    }

    @FXML
    public void initialize() {
        System.out.println(album.getId());
        imageService = new ImageService();
        images = imageService.getImagesForAlbum(album);

        int col = 0;
        int row = 0;
        for (Image image : images) {
            imageGrid.add(createImagePane(image, imageContainer), col++, row);
            if (col % imageGrid.getColumnCount() == 0) {
                row++;
            }
        }

    }

}
