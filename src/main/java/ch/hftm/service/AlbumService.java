package ch.hftm.service;

import ch.hftm.data.Album;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AlbumService {
    public void createAlbum(String name, String description) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        objectMapper.setDateFormat(df);
        Album album = new Album(name, description);
        objectMapper.writeValue(new File("storage/albums.json"), album);

    }
}
