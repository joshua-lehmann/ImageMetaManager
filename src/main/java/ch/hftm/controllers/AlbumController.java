package ch.hftm.controllers;

import ch.hftm.data.Album;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class AlbumController {

    private Album album;

    @FXML
    private TextField albumName;
    @FXML
    private TextField albumDescription;

    @FXML
    private Label albumTitle;

    public void setAlbum(Album album) {
        this.album = album;
        setupAlbumPage();
    }

    private void setupAlbumPage() {
        if (album == null) {
            return;
        }
        albumTitle.setText(album.getName());
        // TODO: Get all images of album and display them nicely in a grid
    }

}
