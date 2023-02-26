package ch.hftm.controllers;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import ch.hftm.data.Image;
import ch.hftm.service.AlbumService;
import ch.hftm.service.ExifService;
import javafx.event.ActionEvent;
import ch.hftm.service.ExifService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
public class ImageController {
    
    private ExifService exifService;
    private Image image;
    private boolean editMode = false;

    @FXML
    private Label imageName;

    @FXML
    private ImageView imageView;

    @FXML
    private BorderPane metaBorderPane;

    @FXML
    private GridPane imageMetaPane;
    @FXML
    private Button editTagButton;


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

        this.imageMetaPane = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35.0);
        col1.setHalignment(HPos.CENTER);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65.0);
        col2.setHalignment(HPos.CENTER);
        this.imageMetaPane.getColumnConstraints().add(col1);
        this.imageMetaPane.getColumnConstraints().add(col2);

        int row = 0;
        createLabel(this.imageMetaPane, 0, row, "Image Name:", "imageNameLabel");
        createLabel(this.imageMetaPane, 1, row++, image.getFileName(), "imageNameValueLabel");

        Map<String, Object> imageTags = exifService.getExifTags(image, true);


        Map<String, Object> imageTags = exifService.getExifTags(image, true);

        for (Map.Entry<String, Object> entry : imageTags.entrySet()) {
            createLabel(this.imageMetaPane, 0, row, entry.getKey(), "metaNameLabel");
            createTexField(this.imageMetaPane, 1, row, entry.getValue().toString(), entry.getKey() + "Value");
            createLabel(this.imageMetaPane, 1, row++, entry.getValue().toString(), "metaValueLabel");
        }

        metaBorderPane.setCenter(this.imageMetaPane);
    }

    private void createLabel(GridPane pane, int column, int row, String labelText, String id) {
        Label metaLabel = new Label();
        metaLabel.setId(id);
        metaLabel.setText(labelText);
        metaLabel.setVisible(true);
        pane.add(metaLabel, column, row);
    }

    private void createTexField(GridPane pane, int column, int row, String value, String id) {
        TextField metaField = new TextField();
        metaField.setId(id);
        metaField.setText(value);
        metaField.setVisible(false);
        metaField.setMaxWidth(120);
        pane.add(metaField, column, row);
    }

    @FXML
    void editTags(ActionEvent event) {
        this.editMode = !this.editMode;
        if (this.editMode) {
            editTagButton.setText("Save Tags");
        } else {
            editTagButton.setText("Edit Tags");
        }
        this.imageMetaPane.getChildren().forEach(node -> {
            if (node instanceof Label label) {
                if (label.getId().equals("metaValueLabel")) {
                    label.setVisible(!this.editMode);
                }
            }
            if (node instanceof TextField textField) {
                textField.setVisible(this.editMode);
            }
        });
        Map<String, Object> currentTags = exifService.getExifTags(image, true);
        for (Map.Entry<String, Object> entry : currentTags.entrySet()) {
            TextField textField = (TextField) this.imageMetaPane.lookup("#" + entry.getKey() + "Value");
            TagInfo tag = ExifService.extendedTags.get(entry.getKey());
            if (textField != null && tag != null) {
                exifService.updateExifTag(image, tag, textField.getText());
            }
        }
    public void initialize() {
        exifService = new ExifService();
    }


}
