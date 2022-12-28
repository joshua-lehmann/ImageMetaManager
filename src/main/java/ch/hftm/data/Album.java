package ch.hftm.data;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class Album {
    private String id;
    private final String name;
    private final String description;
    private Date createdAt = new Date();

    @JsonGetter("id")
    public String getId() {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        String generatedId = name + "_" + df.format(createdAt);
        // removing all blanks from the generated ID
        return generatedId.replaceAll("\\s", "");
    }
}
