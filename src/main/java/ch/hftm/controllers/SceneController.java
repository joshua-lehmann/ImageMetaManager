package ch.hftm.controllers;

import java.io.IOException;

import ch.hftm.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {

    public <T> void changeScene(Scene scene, String sceneName, T data) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(String.format("view/%s.fxml", sceneName)));
        Parent root = fxmlLoader.load();

        if (data != null) {
            var controller = fxmlLoader.getController();
            if (controller instanceof AlbumController albumController && data instanceof ch.hftm.data.Album album) {
                albumController.setAlbum(album);
            } else if (controller instanceof ImageController imageController && data instanceof ch.hftm.data.Image image) {
                imageController.setImage(image);
            }
        } 

        Stage stage = (Stage) scene.getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
