package ch.hftm;

import ch.hftm.service.AlbumService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * JavaFX App
 */
@Slf4j
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("view/library.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        log.debug("Opening first scene");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        log.info("Starting the App");
        AlbumService albumService = new AlbumService();
        albumService.createTestData();
        launch();
    }


}