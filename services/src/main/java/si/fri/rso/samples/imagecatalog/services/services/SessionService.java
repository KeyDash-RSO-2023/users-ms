package si.fri.rso.samples.imagecatalog.services.services;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionService {

    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public static LocalDateTime generateSessionValidUntil() {
        return LocalDateTime.now().plusHours(1);
    }

    public static boolean checkSessionId() {
        return true;
    }
}
