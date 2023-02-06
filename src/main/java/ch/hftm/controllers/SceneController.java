package ch.hftm.controllers;

import ch.hftm.App;
import ch.hftm.data.Album;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public <T> void changeScene(Event event, String sceneName, T data) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(String.format("view/%s.fxml", sceneName)));
        root = fxmlLoader.load();

        var controller = fxmlLoader.getController();
        if (controller instanceof AlbumController && data instanceof ch.hftm.data.Album) {
            ((AlbumController) controller).setAlbum((Album) data);
        }

        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
