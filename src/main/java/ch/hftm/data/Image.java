package ch.hftm.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FilenameUtils;

import java.io.File;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"fullPath", "albumId"})
public class Image {
    private String fullPath;
    private String fileName;
    private String fileExtension;
    private String albumId;


    public Image(File file, String albumId) {
        this.fullPath = file.getPath();
        this.fileName = file.getName();
        this.fileExtension = FilenameUtils.getExtension(fileName);
        this.albumId = albumId;
    }


}
