package ch.hftm.service;

import ch.hftm.data.Album;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AlbumService {

    public static final String STORAGE_ALBUMS_JSON = System.getProperty("albums-json", System.getenv("USERPROFILE") + "\\image-meta-manager\\albums.json");

    public AlbumService() {
        try {
            File storageFile = new File(STORAGE_ALBUMS_JSON);
            File storageDir = storageFile.getParentFile();
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            if (!storageFile.exists()) {
                Files.write(Path.of(storageFile.getPath()), "[]".getBytes());
            }
        } catch (IOException e) {
            log.error("Could not create albums file:{} {}", STORAGE_ALBUMS_JSON, e.getMessage());
        }
    }

    public Album createAlbum(String name, String description) {
        List<Album> existingAlbums = getAllAlbums();
        Album album = new Album(name, description);
        existingAlbums.add(album);
        writeAlbumsToJson(existingAlbums);
        return album;
    }


    public List<Album> getAllAlbums() {
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        objectMapper.setDateFormat(df);
        List<Album> allAlbums = new ArrayList<>();
        try {
            allAlbums = objectMapper.readValue(new File(STORAGE_ALBUMS_JSON), new TypeReference<List<Album>>() {
            });
        } catch (Exception e) {
            log.error("Could not read albums from file:{}, {}", STORAGE_ALBUMS_JSON, e.getMessage());
        }
        return allAlbums;
    }

    public Album getAlbumByName(String name) {
        for (Album album : getAllAlbums()) {
            if (album.getName().equals(name)) {
                return album;
            }
        }
        log.warn("No album found with name {}", name);
        // TODO: Replace with optionals
        return null;
    }

    public Album deleteAlbum(Album albumToRemove) {
        List<Album> existingAlbums = getAllAlbums();
        for (Album album : existingAlbums) {
            if (album.equals(albumToRemove)) {
                existingAlbums.remove(album);
                writeAlbumsToJson(existingAlbums);
                return album;
            }
        }
        log.warn("No album found with name {}", albumToRemove.getName());
        // TODO: Replace with optionals
        return null;
    }

    public void createTestData() {
        if (!getAllAlbums().isEmpty()) {
            return;
        }
        ImageService imageService = new ImageService();
        Album album = createAlbum("Vacation", "Vacation Album");
        imageService.createImage(new File("src/test/resources/DSCN0010.jpg"), album);
        Album album2 = createAlbum("Test Album", "Test Album");
        imageService.createImage(new File("src/test/resources/DSCN0021.jpg"), album2);
        imageService.createImage(new File("src/test/resources/DSCN0012.jpg"), album2);
    }

    private void writeAlbumsToJson(List<Album> existingAlbums) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        objectMapper.setDateFormat(df);
        try {
            File storageFile = new File(STORAGE_ALBUMS_JSON);
            objectMapper.writeValue(storageFile, existingAlbums);
        } catch (IOException e) {
            log.error("Could not write album to file:{} {}", STORAGE_ALBUMS_JSON, e.getMessage());
        }
    }
}
