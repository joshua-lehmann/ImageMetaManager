package ch.hftm.service;

import ch.hftm.data.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ExifService {

    private final Map<String, TagInfo> tags = Map.of(
            "Date taken", ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL,
            "Camera Make", TiffTagConstants.TIFF_TAG_MAKE,
            "Camera Model", TiffTagConstants.TIFF_TAG_MODEL);
    private final Map<String, TagInfo> extendedTags = Stream.concat(Map.of(
                    "Programm", TiffTagConstants.TIFF_TAG_SOFTWARE,
                    "Width", ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH,
                    "Length", ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH).entrySet().stream(),
            tags.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    public Object getExifTag(Image image, TagInfo tagInfo) {
        try {
            File imageFile = new File(image.getFullPath());
            final ImageMetadata metadata = Imaging.getMetadata(imageFile);
            if (metadata instanceof JpegImageMetadata jpegMetadata) {
                TiffField field = jpegMetadata.findEXIFValue(tagInfo);
                return field.getValue();
            }
        } catch (NullPointerException | ImageReadException | IOException e) {
            log.error("Could not read exif data from image {}: {}", image.getFullPath(), e.getMessage());
        }
        return null;
    }

    public Map<String, Object> getTags(Image image, Boolean extended) {
        Map<String, Object> exifData = new HashMap<>();
        // loop over all tags
        for (Map.Entry<String, TagInfo> entry : Boolean.TRUE.equals(extended) ? extendedTags.entrySet() : tags.entrySet()) {
            // get the value of the tag
            Object value = getExifTag(image, entry.getValue());
            // if the value is not null, add it to the map
            if (value != null) {
                exifData.put(entry.getKey(), value);
            }
        }
        return exifData;
    }


}
