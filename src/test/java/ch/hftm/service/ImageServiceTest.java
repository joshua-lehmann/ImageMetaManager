package ch.hftm.service;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

class ImageServiceTest {

    private static final File jsonFile = new File("storage/images.json");
    ImageService imageService = new ImageService();
    AlbumService albumService = new AlbumService();

    @Test
    void createImage() {
        Album album = albumService.getAlbumByName("Vacation");
        Album newAlbum = new Album("Test", "Test Album");
        imageService.createImage(new File("src/test/resources/DSCN0012.jpg"), album);
        imageService.createImage(new File("src/test/resources/DSCN0021.jpg"), album);
        imageService.createImage(new File("src/test/resources/DSCN0010.jpg"), newAlbum);
    }

    @Test
    void getAllImages() {
    }

    @Test
    void getImagesForAlbum() {
        List<Image> vacationImages = imageService.getImagesForAlbum(albumService.getAlbumByName("Vacation"));
//        assertEquals(2, vacationImages.size());
    }
}