package ch.hftm.service;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ImageService {
    public void createImage(File file, Album album) {
        Image image = new Image(file, album.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Image> existingImages = getAllImages();
        existingImages.add(image);
        try {
            objectMapper.writeValue(new File("storage/images.json"), existingImages);
        } catch (Exception e) {
            log.error("Could not write image to file", e.getMessage());
        }
    }

    public List<Image> getAllImages() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Image> allImages = new ArrayList<>();
        try {
            allImages = objectMapper.readValue(new File("storage/images.json"), new TypeReference<List<Image>>() {
            });
        } catch (Exception e) {
            log.error("Could not read images from file", e.getMessage());
        }
        return allImages;
    }

    public List<Image> getImagesForAlbum(Album album) {
        List<Image> imagesForAlbum = new ArrayList<>();
        for (Image image : getAllImages()) {
            if (image.getAlbumId().equals(album.getId())) {
                imagesForAlbum.add(image);
            }
        }
        log.info("Found {} images for album {}", imagesForAlbum.size(), album.getName());
        return imagesForAlbum;
    }
}
