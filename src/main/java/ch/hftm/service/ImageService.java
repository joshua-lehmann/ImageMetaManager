package ch.hftm.service;

import ch.hftm.data.Album;
import ch.hftm.data.Image;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ImageService {

    public static final String STORAGE_IMAGES_JSON = System.getProperty("images-json", System.getenv("USERPROFILE") + "\\image-meta-manager\\images.json");


    public ImageService() {
        try {
            File storageFile = new File(STORAGE_IMAGES_JSON);
            File storageDir = storageFile.getParentFile();
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            if (!storageFile.exists()) {
                Files.write(Path.of(storageFile.getPath()), "[]".getBytes());
            }
        } catch (IOException e) {
            log.error("Could not create images file:{} {}", STORAGE_IMAGES_JSON, e.getMessage());
        }
    }

    public Image createImage(File file, Album album) {
        Image image = new Image(file, album.getId());
        List<Image> existingImages = getAllImages();
        existingImages.add(image);
        writeImagesToJson(existingImages);
        return image;
    }

    public List<Image> getAllImages() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Image> allImages = new ArrayList<>();
        try {
            allImages = objectMapper.readValue(new File(STORAGE_IMAGES_JSON), new TypeReference<List<Image>>() {
            });
        } catch (Exception e) {
            log.error("Could not read images from file {}", e.getMessage());
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

    public Image getImageByFileName(String fileName) {
        for (Image image : getAllImages()) {
            if (image.getFileName().equals(fileName)) {
                return image;
            }
        }
        log.warn("No image found with name {}", fileName);
        // TODO: Replace with optionals
        return null;
    }

    public Image deleteImage(Image imageToRemove) {
        List<Image> existingImages = getAllImages();
        for (Image existingImage : existingImages) {
            if (existingImage.equals(imageToRemove)) {
                existingImages.remove(existingImage);
                log.info("Image {} deleted", imageToRemove.getFileName());
                writeImagesToJson(existingImages);
                return imageToRemove;
            }
        }
        log.warn("Image {} could not be deleted because it was not found", imageToRemove.getFileName());
        // TODO: Replace with optionals
        return null;
    }

    private static void writeImagesToJson(List<Image> newImages) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File storageFile = new File(STORAGE_IMAGES_JSON);
            objectMapper.writeValue(storageFile, newImages);
        } catch (Exception e) {
            log.error("Could not write images to file: {} {}", STORAGE_IMAGES_JSON, e.getMessage());
        }
    }
}
