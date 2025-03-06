package voteapp.geostorageservice.utils;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.experimental.UtilityClass;
import voteapp.geostorageservice.exception.BadRequestException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

@UtilityClass
public class ImageUtils {

    private final String folder = "images/";

    public static boolean compareMetadata(ObjectMetadata metadata1, ObjectMetadata metadata2) {
        return metadata1.getContentLength() == metadata2.getContentLength() &&
                metadata1.getUserMetaDataOf(ImageParameters.WIDTH.toString()).equals(metadata2.getUserMetaDataOf(ImageParameters.WIDTH.toString())) &&
                metadata1.getUserMetaDataOf(ImageParameters.HEIGHT.toString()).equals(metadata2.getUserMetaDataOf(ImageParameters.HEIGHT.toString()));
    }

    public static Map<ImageParameters, Integer> getWidthAndHeightImage(ByteArrayInputStream file) {
        try {
            BufferedImage image = ImageIO.read(file);
            EnumMap<ImageParameters, Integer> map = new EnumMap<>(ImageParameters.class);
            map.put(ImageParameters.WIDTH, image.getWidth());
            map.put(ImageParameters.HEIGHT, image.getHeight());

            return map;
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public static String getImageIdForDb(String path) {

        if(!path.contains(folder)) {
            return path + StaticWords.REMOVE;
        }

        return path.substring(path.lastIndexOf(folder));
    }
}
