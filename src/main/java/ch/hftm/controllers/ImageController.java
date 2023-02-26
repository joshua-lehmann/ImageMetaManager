package ch.hftm.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ch.hftm.data.Image;
import ch.hftm.service.AlbumService;
import ch.hftm.service.ExifService;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageController {
    
    private ExifService exifService;
    private Image image;

    @FXML
    private Label imageName;

    @FXML
    private ImageView imageView;

    @FXML
    private BorderPane metaBorderPane;
    
    public void setImage(Image image) {
        if (image == null) {
            return;
        }
        this.image = image;
        initializeImage();
    }

    @FXML
    public void goToAlbum() {
        SceneController sceneController = new SceneController();
        try {
            AlbumService albumService = new AlbumService();
            sceneController.changeScene(metaBorderPane.getScene(), "album", albumService.getAlbumById(image.getAlbumId()));
        } catch (IOException e) {
            log.error("Scene change failed: ", e.getMessage());
        }
    }

    private void initializeImage() {
        javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new File(image.getFullPath()).toURI().toString());
        imageView.setImage(fxImage);
        
        GridPane imageMetaPane = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35.0);
        col1.setHalignment(HPos.CENTER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65.0);
        col2.setHalignment(HPos.CENTER);
        imageMetaPane.getColumnConstraints().add(col1);
        imageMetaPane.getColumnConstraints().add(col2);

        int row = 0;
        createLabel(imageMetaPane, 0, row, "Image Name:");
        createLabel(imageMetaPane, 1, row++, image.getFileName());

        Map<String, Object> imageTags = exifService.getTags(image, true);

        
        for (Map.Entry<String, Object> entry : imageTags.entrySet()) {
            createLabel(imageMetaPane, 0, row, entry.getKey());
            createLabel(imageMetaPane, 1, row++, entry.getValue().toString());
        }

        metaBorderPane.setCenter(imageMetaPane);
    }

    private void createLabel(GridPane pane, int column, int row, String labelText) {
        Label label = new Label();
        label.setText(labelText);
        label.setVisible(true);
        pane.add(label, column, row);
    }

    @FXML
    public void initialize() {
        exifService = new ExifService();
    }
}
