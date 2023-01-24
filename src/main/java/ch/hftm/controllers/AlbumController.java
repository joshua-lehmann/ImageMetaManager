package ch.hftm.controllers;

import ch.hftm.service.AlbumService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class AlbumController {

    @FXML
    private TextField albumName;
    @FXML
    private TextField albumDescription;

    public void createAlbum(ActionEvent event) {
        System.out.println("Create Album");
        System.out.println(albumName.getText() + albumDescription.getText());

        AlbumService albumService = new AlbumService();
        albumService.createAlbum(albumName.getText(), albumDescription.getText());
    }
}
