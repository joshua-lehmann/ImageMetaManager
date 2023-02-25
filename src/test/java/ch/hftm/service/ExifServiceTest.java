package ch.hftm.service;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExifServiceTest {

    ImageService imageService = new ImageService();
    AlbumService albumService = new AlbumService();
    ExifService exifService = new ExifService();

    @Test
    void getExifTag() {
        Album newAlbum = albumService.createAlbum("Test", "Test Album");
        imageService.createImage(new File("src/test/resources/DSCN0012.jpg"), newAlbum);
        imageService.createImage(new File("src/test/resources/DSCN0021.jpg"), newAlbum);
        Image image = imageService.createImage(new File("src/test/resources/DSCN0010.jpg"), newAlbum);
        Map<String, Object> tags = exifService.getTags(image, false);
        String dateTaken = tags.get("Date taken").toString();
        String cameraMake = tags.get("Camera Make").toString();
        String cameraModel = tags.get("Camera Model").toString();
        assertEquals("2008:10:22 16:28:39", dateTaken);
        assertEquals("NIKON", cameraMake);
        assertEquals("COOLPIX P6000", cameraModel);
    }

    @Test
    void getExtendedExifTag() {
        Album newAlbum = albumService.createAlbum("Test", "Test Album");
        imageService.createImage(new File("src/test/resources/DSCN0012.jpg"), newAlbum);
        imageService.createImage(new File("src/test/resources/DSCN0021.jpg"), newAlbum);
        Image image = imageService.createImage(new File("src/test/resources/DSCN0010.jpg"), newAlbum);
        Map<String, Object> tags = exifService.getTags(image, true);
        String programm = tags.get("Programm").toString();
        String length = tags.get("Length").toString();
        String width = tags.get("Width").toString();
        assertEquals("Nikon Transfer 1.1 W", programm);
        assertEquals("480", length);
        assertEquals("640", width);
    }
}