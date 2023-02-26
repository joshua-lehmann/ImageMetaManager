package ch.hftm.controllers;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import ch.hftm.service.ExifService;
import ch.hftm.service.ImageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
public class AlbumController {

    public static final String STORAGE_DIRECTORY_JSON = System.getProperty("defaultDir-json", System.getenv("USERPROFILE") + "\\image-meta-manager\\defaultDir.json");
    private static final String FALLBACK_DIR = System.getenv("USERPROFILE");
    private static final String DEFAULT_TITLE = "Invalid Selection";

    private ImageService imageService;
    private ExifService exifService;

    private Album album;
    private final List<Image> imageSelection = new ArrayList<>();

    @FXML
    private BorderPane albumPane;

    @FXML
    private Label albumTitle;

    @FXML
    private Label albumStatus;

    @FXML
    private GridPane imageGrid;

    @FXML
    private Pane imageContainer;

    public void setAlbum(Album album) {
        this.album = album;
        setupAlbumPage();
    }

    private void setupAlbumPage() {
        if (album == null) {
            return;
        }
        albumTitle.setText("Album: " + album.getName());
        initializeAlbum();
    }

    @FXML
    public void goToLibrary() {
        SceneController sceneController = new SceneController();
        try {
            sceneController.changeScene(albumPane.getScene(), "library", null);
        } catch (IOException e) {
            log.error("Scene change failed: {}", e.getMessage());
        }
    }

    /**
     * Create a pane to display an image including the meta attribut descriptors and values
     */
    private Pane createImagePane(Image image, Pane samplePane) {
        Pane imagePane = new Pane();
        imagePane.setPrefHeight(samplePane.getPrefHeight());
        imagePane.setPrefWidth(samplePane.getPrefWidth());
        imagePane.setOnMouseClicked(event -> {
            Node source = (Node) event.getSource();
            Label imageName = (Label) source.lookup("#imageName");
            Image imageToDelete = imageService.getImageByFileName(imageName.getText());
            if (source.getStyle().equals("")) {
                source.setStyle("-fx-border-style: solid; -fx-border-width: 3; -fx-border-color: blue;");
                imageSelection.add(imageToDelete);
            } else {
                source.setStyle(null);
                imageSelection.remove(imageToDelete);
            }
        });

        Label imageName = new Label(image.getFileName());
        imageName.setId("imageName");
        imageName.setVisible(false);

        ImageView sampleImageView = (ImageView) samplePane.lookup("#imageView");
        ImageView imageView = new ImageView();
        imageView.setFitHeight(sampleImageView.getFitHeight());
        imageView.setFitWidth(sampleImageView.getFitWidth());
        javafx.scene.image.Image fxImage = new javafx.scene.image.Image(new File(image.getFullPath()).toURI().toString());
        imageView.setImage(fxImage);

        GridPane imageMetaPane = new GridPane();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(35.0);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(65.0);
        imageMetaPane.getColumnConstraints().add(col1);
        imageMetaPane.getColumnConstraints().add(col2);

        Map<String, Object> imageTags = exifService.getExifTags(image, false);
        
        int row = 0;
        for (Map.Entry<String, Object> entry : imageTags.entrySet()) {
            createLabel(imageMetaPane, 0, row, entry.getKey() + ":");
            createLabel(imageMetaPane, 1, row++, entry.getValue().toString());
        }

        BorderPane imageBorderPane = new BorderPane();
        imageBorderPane.setTop(imageView);
        BorderPane.setMargin(imageView, new Insets(5, 5, 0, 5));
        imageBorderPane.setCenter(imageMetaPane);
        BorderPane.setMargin(imageMetaPane, new Insets(10, 10, 0, 10));

        imagePane.getChildren().add(imageBorderPane);
        imagePane.getChildren().add(imageName);
        return imagePane;
    }

    private void createLabel(GridPane pane, int column, int row, String labelText) {
        Label label = new Label();
        label.setText(labelText);
        label.setVisible(true);
        pane.add(label, column, row);
    }

    @FXML
    public void deleteImage() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete selected images");
        alert.setContentText("If you want to delete the selected images press OK.");
        ButtonType okButton = new ButtonType("OK");
        ButtonType cancelButton = new ButtonType("Cancel");
        alert.getButtonTypes().setAll(okButton, cancelButton);
        Optional<ButtonType> choice = alert.showAndWait();

        if (choice.isEmpty() || choice.get() != okButton) {
            for (Node child : imageGrid.getChildren()) {
                child.setStyle(null);
            }
            imageSelection.clear();
            return;
        }
        if (imageSelection.isEmpty()) {
            alertUser("", "No images were selected.", AlertType.WARNING);
            imageSelection.clear();
            return;
        }
        int i = 0;
        for (Image image : imageSelection) {
            imageService.deleteImage(image);
            i++;
        }
        updateAlbumStatus(String.format("Deleted %d image(s).", i), 5_000);
        initializeAlbum();
    }

    @FXML
    public void addImage() {
        // Create a FileChooser in the default directory and only show files of the type jpg, png
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(getDefaultDirectory());
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
        );

        // Create a new image in the current album if a supported file was selected
        // Otherwise inform the user that no image could be added
        File selectedFile = fileChooser.showOpenDialog(albumPane.getScene().getWindow());
        if (selectedFile != null) {
            // Overwrite the default directory to the current directory
            Path newPath = Paths.get(selectedFile.getAbsolutePath());
            writeDefaultDirectoryToJson(newPath.getParent().toString());

            Image newImage = imageService.createImage(selectedFile, album);
            initializeAlbum();
            updateAlbumStatus(String.format("New image %s was added", newImage.getFileName()), 5_000);
        } else {
            alertUser(DEFAULT_TITLE, "No image can be added because no image was selected.", AlertType.INFORMATION);
        }
        imageSelection.clear();
    }

    private void writeDefaultDirectoryToJson(String directoryPath) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            File storageFile = new File(STORAGE_DIRECTORY_JSON);
            File storageDir = storageFile.getParentFile();
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            if (!storageFile.exists()) {
                Files.write(Path.of(storageFile.getPath()), "[]".getBytes());
            }

            objectMapper.writeValue(storageFile, directoryPath);
        } catch (Exception e) {
            log.error("Could not write default directory to file: {} {}", STORAGE_DIRECTORY_JSON, e.getMessage());
        }
    }

    /**
     *
     * @return If the last used directory is stored in JSON, the stored directory is returned.
     *         Otherwise a fall-back directory is returned.
     */
    private File getDefaultDirectory() {
        File storageFile = new File(STORAGE_DIRECTORY_JSON);
        if (storageFile.exists()) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String directory = objectMapper.readValue(storageFile, new TypeReference<String>() {});
                return new File(directory);
            } catch (Exception e) {
                log.error("Could not read default directory from file {}", e.getMessage());
            }
        }
        return new File(FALLBACK_DIR);
    }

    /**
     * @param status   The status to be shown in the album view
     * @param duration The status is shown for this duration
     */
    private void updateAlbumStatus(String status, double duration) {
        albumStatus.setVisible(true);
        albumStatus.setText(status);
        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(duration),
                        kf -> albumStatus.setText("")));
        timeline.play();
        timeline.setOnFinished(e -> albumStatus.setVisible(false));
    }

    public void initializeAlbum() {
        // Clear the imageGrid of all children nodes
        imageGrid.getChildren().clear();

        List<Image> images = imageService.getImagesForAlbum(album);

        int col = 0;
        int row = 0;
        // Create an pane for each image in the album
        for (Image image : images) {
            imageGrid.add(createImagePane(image, imageContainer), col++, row);
            // Place the image on a new row if the current column is full of images
            if (col % imageGrid.getColumnCount() == 0) {
                row++;
                col = 0;
            }
        }

    }

    @FXML
    public void editImage() {
        String title = "";
        String content;
        if (imageSelection.size() == 1) {
            Image imageToEdit = imageSelection.get(0);
            Scene scene = albumPane.getScene();

            SceneController sceneController = new SceneController();
            try {
                sceneController.changeScene(scene, "image", imageToEdit);
            } catch (IOException e) {
                log.error(String.format("The image %s could not be edited: %s", imageToEdit.getFileName(), e.getMessage()));
            } finally {
                imageSelection.clear();
            }
        } else if (imageSelection.isEmpty()) {
            content = "You need to select an image you want to edit.";
            alertUser(title, content, AlertType.WARNING);
        } else {
            content = "Only one image at a time can be edited.";
            alertUser(title, content, AlertType.WARNING);
        }
    }

    private void alertUser(String title, String content, AlertType alertType) {
        if (title.equals("")) {
            title = DEFAULT_TITLE;
        }
        Alert info = new Alert(alertType);
        info.setTitle(title);
        info.setContentText(content);
        info.showAndWait();
    }

    @FXML
    public void initialize() {
        imageService = new ImageService();
        exifService = new ExifService();
    }

}
