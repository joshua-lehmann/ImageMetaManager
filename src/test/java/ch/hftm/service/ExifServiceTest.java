package ch.hftm.service;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class ExifServiceTest {

    ImageService imageService = new ImageService();
    AlbumService albumService = new AlbumService();
    ExifService exifService = new ExifService();

    @BeforeAll
    static void setup() {
        System.setProperty("images-json", "src/test/resources/storage/images.json");
        System.setProperty("albums-json", "src/test/resources/storage/albums.json");
        try {
            Files.deleteIfExists(Path.of("src/test/resources/storage/images.json"));
            Files.deleteIfExists(Path.of("src/test/resources/storage/albums.json"));
        } catch (IOException e) {
            log.error("Could not delete test albums file:{}", e.getMessage());
        }
    }

    @Test
    void getExifTags() {
        Album newAlbum = albumService.createAlbum("Test", "Test Album");
        imageService.createImage(new File("src/test/resources/DSCN0012.jpg"), newAlbum);
        imageService.createImage(new File("src/test/resources/DSCN0021.jpg"), newAlbum);
        Image image = imageService.createImage(new File("src/test/resources/DSCN0010.jpg"), newAlbum);
        Map<String, Object> tags = exifService.getExifTags(image, false);
        String dateTaken = tags.get("Date taken").toString();
        String cameraMake = tags.get("Camera Make").toString();
        String cameraModel = tags.get("Camera Model").toString();
        assertEquals("2008:10:22 16:28:39", dateTaken);
        assertEquals("NIKON", cameraMake);
        assertEquals("COOLPIX P6000", cameraModel);
    }

    @Test
    void getExtendedExifTags() {
        Album newAlbum = albumService.createAlbum("Test", "Test Album");
        Image image = imageService.createImage(new File("src/test/resources/DSCN0010.jpg"), newAlbum);
        Map<String, Object> tags = exifService.getExifTags(image, true);
        String programm = tags.get("Programm").toString();
        String length = tags.get("Length").toString();
        String width = tags.get("Width").toString();
        assertEquals("Nikon Transfer 1.1 W", programm);
        assertEquals("480", length);
        assertEquals("640", width);
    }

    @Test
    void updateExifTag() {
        Album newAlbum = albumService.createAlbum("Test", "Test Album");
        Image image = imageService.createImage(new File("src/test/resources/DSCN0010.jpg"), newAlbum);
        exifService.updateExifTag(image, new File("src/test/resources/DSCN0010_new.jpg"), ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, "2022:01:01 16:16:16");
        exifService.updateExifTag(image, new File("src/test/resources/DSCN0010_new.jpg"), ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, Short.valueOf("800"));
    }
}