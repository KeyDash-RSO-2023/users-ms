package si.fri.rso.samples.imagecatalog.services.services;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordService {

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    public static boolean verifyPassword(String enteredPassword, String storedHashedPassword) {
        String hashedEnteredPassword = DigestUtils.sha256Hex(enteredPassword);
        return hashedEnteredPassword.equals(storedHashedPassword);
    }
}
