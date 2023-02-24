package ch.hftm.service;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
        Date value = exifService.getExifTag(image, ExifTagConstants.EXIF_TAG_FOCAL_PLANE_RESOLUTION_UNIT_EXIF_IFD);
//        var value2 = exifService.getExifTag(image, ExifTagConstants.EXIF_TAG_FOCAL_PLANE_RESOLUTION_UNIT_EXIF_IFD);

        var test = "Test";
        assertEquals("2008:10:22 16:28:39", value);
        LocalDateTime date = LocalDateTime.parse(
                value.toString(),
                DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));

//        System.out.println(date);

        var test = ExifService.getType("Test");

    }
}