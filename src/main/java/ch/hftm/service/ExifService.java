package ch.hftm.service;

import ch.hftm.data.Image;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoShort;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.apache.commons.io.FileUtils;

import java.io.*;
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


    private Object getExifTag(Image image, TagInfo tagInfo) {
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

    public Map<String, Object> getExifTags(Image image, Boolean extended) {
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

    public <T extends TagInfo> void updateExifTag(Image image, File destination, T tag, Object value) {
        try (FileOutputStream fos = new FileOutputStream(destination); OutputStream os = new BufferedOutputStream(fos)) {
            File imageFile = new File(image.getFullPath());
            final ImageMetadata metadata = Imaging.getMetadata(imageFile);
            TiffOutputSet outputSet = null;
            if (metadata instanceof JpegImageMetadata jpegMetadata) {
                final TiffImageMetadata exif = jpegMetadata.getExif();
                if (null != exif) {
                    // if we have existing EXIF metadata, we can want to get a copy which we can modify.
                    outputSet = exif.getOutputSet();
                } else {
                    // if we don't have existing EXIF metadata, we create an empty set of EXIF metadata.
                    outputSet = new TiffOutputSet();
                }

                final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
                // make sure to remove old value if present (this method will not fail if the tag does not exist).
                exifDirectory.removeField(tag);

                if (tag instanceof TagInfoAscii)
                    exifDirectory.add((TagInfoAscii) tag, value.toString());
                else if (tag instanceof TagInfoShort)
                    exifDirectory.add((TagInfoShort) tag, Short.valueOf(value.toString()));

                new ExifRewriter().updateExifMetadataLossless(imageFile, os,
                        outputSet);
                // Somehow overwriting the original file with new tags does not work, so we copy the new file to the original file
                FileUtils.delete(imageFile);
                FileUtils.copyFile(destination, imageFile, true);
                FileUtils.delete(destination);
            }

        } catch (NullPointerException | ImageReadException | IOException | ImageWriteException e) {
            log.error("Could not read exif data from image {}: {}", image.getFullPath(), e.getMessage());
        }

    }


}
