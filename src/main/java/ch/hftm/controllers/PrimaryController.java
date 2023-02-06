package ch.hftm.controllers;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import ch.hftm.service.AlbumService;
import ch.hftm.service.ImageService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    @FXML
    private ScrollPane scrollPaneLibrary;

    @FXML
    private AnchorPane anchorPaneLibrary;

    @FXML
    private Button albumButton;

    @FXML
    private Pane albumContainer;

    @FXML
    private GridPane albumGrid;


    public void onAlbumClick(MouseEvent event) throws IOException {
        AlbumService albumService = new AlbumService();
        var source = (Node) event.getSource();
        Label titleLabel = (Label) source.lookup("#albumTitle");
        Album album = albumService.getAlbumByName(titleLabel.getText());
        SceneController sceneController = new SceneController();
        sceneController.changeScene(event, "album", album);
    }


    public Pane createAlbumPane(Album album, Pane samplePane) {
        ImageService imageService = new ImageService();
        Label sampleTitleLabel = (Label) samplePane.lookup("#albumTitle");
        Label albumTitle = new Label(album.getName());
        albumTitle.setLayoutX(sampleTitleLabel.getLayoutX());
        albumTitle.setLayoutY(sampleTitleLabel.getLayoutY());
        albumTitle.setAlignment(sampleTitleLabel.getAlignment());
        albumTitle.setTextAlignment(sampleTitleLabel.getTextAlignment());
        albumTitle.setContentDisplay(sampleTitleLabel.getContentDisplay());
        albumTitle.setPrefWidth(sampleTitleLabel.getPrefWidth());
        albumTitle.setId(sampleTitleLabel.getId());

        Label sampleIdLabel = (Label) samplePane.lookup("#albumId");
        Label albumId = new Label(album.getId());
        albumId.setId(sampleIdLabel.getId());
        albumId.setVisible(false);

        Image coverImage = imageService.getImagesForAlbum(album).get(0);

        Pane albumPane = new Pane();
        albumPane.setPrefHeight(samplePane.getPrefHeight());
        albumPane.setPrefWidth(samplePane.getPrefWidth());
        albumPane.setOnMouseClicked(samplePane.getOnMouseClicked());

        ImageView sampleImageView = (ImageView) samplePane.lookup("#albumImage");

        ImageView albumImageView = new ImageView();
        albumImageView.setFitHeight(sampleImageView.getFitHeight());
        albumImageView.setFitWidth(sampleImageView.getFitWidth());
        albumImageView.setPickOnBounds(true);

        javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new File(coverImage.getFullPath()).toURI().toString());
        albumImageView.setImage(fxImage);
        sampleImageView.setImage(fxImage);
        albumPane.getChildren().add(albumImageView);
        albumPane.getChildren().add(albumTitle);
        albumPane.getChildren().add(albumId);
        return albumPane;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AlbumService albumService = new AlbumService();
        List<Album> albums = albumService.getAllAlbums();

        int row = 0;
        for (Album album : albums) {
            albumGrid.add(createAlbumPane(album, albumContainer), 1, row);
            row++;
        }
    }
}
