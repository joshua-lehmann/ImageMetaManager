package ch.hftm.controllers;

import java.io.File;

import ch.hftm.data.Image;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class ImageController {
    
    private Image image;

    @FXML
    private Label imageName;

    @FXML
    private ImageView imageView;
    
    public void setImage(Image image) {
        this.image = image;
        if (image == null) {
            return;
        }
        initializeImage();
    }

    private void initializeImage() {
        imageName.setText(image.getFileName());
        javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new File(image.getFullPath()).toURI().toString());
        imageView.setImage(fxImage);
    }
}
