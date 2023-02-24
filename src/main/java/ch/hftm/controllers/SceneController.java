package ch.hftm.controllers;

import ch.hftm.App;
import ch.hftm.data.Album;
import ch.hftm.data.Image;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {

    public <T> void changeScene(Scene scene, String sceneName, T data) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(String.format("view/%s.fxml", sceneName)));
        Parent root = fxmlLoader.load();

        var controller = fxmlLoader.getController();
        switch (controller) {
            case AlbumController albumController -> albumController.setAlbum((Album) data);
            case ImageController imageController -> imageController.setImage((Image) data);
            default -> throw new IllegalStateException("Unexpected controller: " + controller.getClass().getName());
        }

        Stage stage = (Stage) scene.getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
