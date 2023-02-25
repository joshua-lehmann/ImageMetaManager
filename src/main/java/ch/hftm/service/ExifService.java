package ch.hftm.service;

import ch.hftm.data.Image;
import ch.hftm.data.MyFieldType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoAscii;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class ExifService {

    private List<TagInfoAscii> tags = List.of(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED, ExifTagConstants.EXIF_TAG_LENS_MAKE, ExifTagConstants.EXIF_TAG_LENS_MODEL);
    private List<TagInfo> extendedTags = List.of(ExifTagConstants.EXIF_TAG_SOFTWARE, ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH);

    public Object getExifTag(Image image, TagInfo tagInfo) {
        try {
            File imageFile = new File(image.getFullPath());
            final ImageMetadata metadata = Imaging.getMetadata(imageFile);
            if (metadata instanceof JpegImageMetadata) {
                final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                TiffField field = jpegMetadata.findEXIFValue(tagInfo);
                var test2 = castToCorrectType(field);
                return test2;
            }
        } catch (NullPointerException | ImageReadException | IOException e) {
            log.error("Could not read exif data from image {}: {}", image.getFullPath(), e.getMessage());
        }
        return null;
    }

    public static <T> T getType(T param) {
        return param;
    }


    private <T> T castToCorrectType(TiffField field) throws ImageReadException {
        Object value = field.getValue();
        switch (MyFieldType.getByType(field.getFieldType().getType())) {
            case DOUBLE -> {
                return (T) Double.valueOf(field.toString());
            }
            case LONG -> {
                return (T) Long.valueOf(value.toString());
            }
            default -> {
                return (T) value.toString();
            }
        }
    }

}
