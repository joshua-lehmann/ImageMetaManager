package ch.hftm.service;

import ch.hftm.data.Album;
import ch.hftm.interfaces.JsonPersisting;
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
public class AlbumService implements JsonPersisting<Album> {

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
        writeDataToJson(existingAlbums);
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
        return null;
    }

    public Album getAlbumById(String id) {
        for (Album album : getAllAlbums()) {
            if (album.getId().equals(id)) {
                return album;
            }
        }
        log.warn("No album found with id {}", id);
        return null;
    }

    public Album deleteAlbum(Album albumToRemove) {
        List<Album> existingAlbums = getAllAlbums();
        for (Album album : existingAlbums) {
            if (album.equals(albumToRemove)) {
                existingAlbums.remove(album);
                writeDataToJson(existingAlbums);
                return album;
            }
        }
        log.warn("No album found with name {}", albumToRemove.getName());
        return null;
    }

    public int getAlbumCount() {
        return getAllAlbums().size();
    }

    public void createTestData() {
        if (!getAllAlbums().isEmpty()) {
            return;
        }
        ImageService imageService = new ImageService();
        Album album = createAlbum("Sample Album", "Album with one image");
        imageService.createImage(new File("src/main/resources/images/DSCN0010.jpg"), album);
        Album album2 = createAlbum("Multi Image Sample", "Album with two images");
        imageService.createImage(new File("src/main/resources/images/DSCN0021.jpg"), album2);
        imageService.createImage(new File("src/main/resources/images/DSCN0012.jpg"), album2);
    }

    @Override
    public void writeDataToJson(List<Album> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        objectMapper.setDateFormat(df);
        try {
            File storageFile = new File(STORAGE_ALBUMS_JSON);
            objectMapper.writeValue(storageFile, data);
        } catch (IOException e) {
            log.error("Could not write album to file:{} {}", STORAGE_ALBUMS_JSON, e.getMessage());
        }
    }
}
