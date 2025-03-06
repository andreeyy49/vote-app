package voteapp.geostorageservice.utils;

import lombok.experimental.UtilityClass;
import voteapp.geostorageservice.exception.BadRequestException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@UtilityClass
public class HashUtil {

    public static String hash(String file)  {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(file);
            MessageDigest digest;
            digest = MessageDigest.getInstance("MD5");
            byte[] hashBytes = digest.digest(imageBytes);

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}
