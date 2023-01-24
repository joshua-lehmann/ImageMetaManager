package ch.hftm.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimaryController implements Initializable {
    @FXML
    private ScrollPane scrollPaneLibrary;

    @FXML
    private AnchorPane anchorPaneLibrary;

    @FXML
    private Button albumButton;

    public PrimaryController() {

    }

    public void goToAlbumPage(ActionEvent event) throws IOException {
        SceneController sceneController = new SceneController();
        sceneController.changeScene(event);
    }

    // Test for adding a Button
//    public void addButton() {
//        anchorPaneLibrary.getChildren().add(new Button("Test"));
//    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        addButton();
    }
}
