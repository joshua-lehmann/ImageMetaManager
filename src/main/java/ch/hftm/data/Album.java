package ch.hftm.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Album {
    private String id;
    private String name;
    private String description;
    private Date createdAt = new Date();


    public Album(String name, String description) {
        this.name = name;
        this.description = description;
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy-HH:mm");
        String generatedId = name + "_" + df.format(createdAt);
        this.id = generatedId;
    }

}
