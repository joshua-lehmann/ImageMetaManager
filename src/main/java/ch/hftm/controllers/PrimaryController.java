package ch.hftm.controllers;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import ch.hftm.service.AlbumService;
import ch.hftm.service.ImageService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
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


    public PrimaryController() {
    }

    public void onAlbumClick(ActionEvent event) throws IOException {
        SceneController sceneController = new SceneController();
        sceneController.changeScene(event, "album");
    }

    // Test for adding a Button
//    public void addButton() {
//        anchorPaneLibrary.getChildren().add(new Button("Test"));
//    }

    public Pane createAlbumPane(Album album) {
        ImageService imageService = new ImageService();
        Pane albumPane = new Pane();
        Label albumTitle = new Label(album.getName());
        Label albumId = new Label(album.getId());
        ImageView albumImage = new ImageView();
        albumImage.setFitWidth(499);
        albumImage.setFitHeight(399);
        Image image = imageService.getImagesForAlbum(album).get(0);
        javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new File(image.getFullPath()).toURI().toString());
        albumImage.setImage(fxImage);
        albumPane.getChildren().add(albumImage);
        albumPane.getChildren().add(albumTitle);
        albumPane.getChildren().add(albumId);
        return albumPane;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        addButton();
        AlbumService albumService = new AlbumService();
        ImageService imageService = new ImageService();
        List<Album> albums = albumService.getAllAlbums();
        int row = 0;
        for (Album album : albums) {
            albumGrid.add(createAlbumPane(album), 0, row);
            row++;
        }
    }
}
