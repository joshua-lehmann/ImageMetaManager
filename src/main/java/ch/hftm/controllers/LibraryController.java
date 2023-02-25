package ch.hftm.controllers;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import ch.hftm.service.AlbumService;
import ch.hftm.service.ImageService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LibraryController implements Initializable {

    static final int ITEMS_PER_ROW = 4;

    @FXML
    private Pane albumContainer;

    @FXML
    private GridPane albumGrid;

    @FXML
    private Button createAlbumButton;

    @FXML
    private TextArea newAlbumDescription;

    @FXML
    private TextField newAlbumTitle;
    @FXML
    private Label numberOfAlbums;


    @FXML
    public void onAlbumClick(MouseEvent event) throws IOException {
        AlbumService albumService = new AlbumService();
        Pane source = (Pane) event.getSource();
        Label titleLabel = (Label) source.lookup("#albumTitle");
        Album album = albumService.getAlbumByName(titleLabel.getText());
        SceneController sceneController = new SceneController();
        Scene scene = ((Node) event.getSource()).getScene();
        sceneController.changeScene(scene, "album", album);
    }

    @FXML
    void createNewAlbum() {
        AlbumService albumService = new AlbumService();
        Album newAlbum = albumService.createAlbum(newAlbumTitle.getText(), newAlbumDescription.getText());
        int itemIndex = albumService.getAlbumCount() - 1;
        int rowIndex = itemIndex / ITEMS_PER_ROW;
        int columnIndex = itemIndex % ITEMS_PER_ROW;
        albumGrid.add(createAlbumPane(newAlbum, albumContainer), columnIndex, rowIndex);
        numberOfAlbums.setText(String.valueOf(albumService.getAlbumCount()));
        newAlbumTitle.setText("");
        newAlbumDescription.setText("");
        createAlbumButton.setDisable(true);
    }

    private boolean canCreateAlbum() {
        return checkRequiredField(newAlbumTitle) && checkRequiredField(newAlbumDescription);
    }

    private boolean checkRequiredField(TextInputControl field) {
        if (field.getText().isEmpty()) {
            field.setStyle("-fx-border-color: red");
            return false;
        } else {
            field.setStyle("-fx-border-color: green");
            return true;
        }
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

        List<Image> images = imageService.getImagesForAlbum(album);
        Image coverImage = images.isEmpty() ? imageService.createImage(new File("src/main/resources/images/placeholderImage.jpg"), album) : images.get(0);


        Pane albumPane = new Pane();
        albumPane.setPrefHeight(samplePane.getPrefHeight());
        albumPane.setPrefWidth(samplePane.getPrefWidth());
        albumPane.setOnMouseClicked(samplePane.getOnMouseClicked());
        albumPane.setId(samplePane.getId());

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


    void checkNewAlbumInput() {
        createAlbumButton.setDisable(!canCreateAlbum());
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AlbumService albumService = new AlbumService();
        List<Album> albums = albumService.getAllAlbums();

        int rowIndex = 0;
        int columnIndex = 0;
        for (Album album : albums) {
            albumGrid.add(createAlbumPane(album, albumContainer), columnIndex, rowIndex);
            columnIndex++;
            if (columnIndex == ITEMS_PER_ROW) {
                columnIndex = 0;
                rowIndex++;
            }
        }

        numberOfAlbums.setText(String.valueOf(albums.size()));

        newAlbumTitle.textProperty().addListener((observable, oldValue, newValue) -> checkNewAlbumInput());
        newAlbumDescription.textProperty().addListener((observable, oldValue, newValue) -> checkNewAlbumInput());


    }
}
