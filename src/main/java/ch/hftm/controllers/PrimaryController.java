package ch.hftm.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;

public class PrimaryController implements Initializable {
    @FXML
    private ScrollPane scrollPaneLibrary;

    @FXML
    private AnchorPane anchorPaneLibrary;

    public PrimaryController() {

    }

    // Test for adding a Button
    public void addButton() {
        anchorPaneLibrary.getChildren().add(new Button("Test"));
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButton();
    }
}
