package ch.hftm.service;

import ch.hftm.data.Album;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AlbumService {
    public void createAlbum(String name, String description) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        objectMapper.setDateFormat(df);
        List<Album> existingAlbums = getAllAlbums();
        Album album = new Album(name, description);
        existingAlbums.add(album);
        try {
            objectMapper.writeValue(new File("storage/albums.json"), existingAlbums);
        } catch (IOException e) {
            log.error("Could not write album to file", e.getMessage());
        }
    }

    public List<Album> getAllAlbums() {
        ObjectMapper objectMapper = new ObjectMapper();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        objectMapper.setDateFormat(df);
        List<Album> allAlbums = new ArrayList<>();
        try {
            allAlbums = objectMapper.readValue(new File("storage/albums.json"), new TypeReference<List<Album>>() {
            });
        } catch (Exception e) {
            log.error("Could not read albums from file", e.getMessage());
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
}
