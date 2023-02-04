package ch.hftm;

import ch.hftm.data.Album;
import ch.hftm.service.AlbumService;
import ch.hftm.service.ImageService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

/**
 * JavaFX App
 */
@Slf4j
public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("view/primary"), 1000, 800);
        log.debug("Opening first scene");
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        log.info("Starting the App");
//        AlbumService albumService = new AlbumService();
//        ImageService imageService = new ImageService();
//        Album album = albumService.createAlbum("Main Vacation", "Vacation Album");
//        imageService.createImage(new File("src/test/resources/DSCN0010.jpg"), album);
//        Album album2 = albumService.createAlbum("Test Album", "Test Album");
//        imageService.createImage(new File("src/test/resources/DSCN0021.jpg"), album2);
        launch();
    }

}