package si.fri.rso.samples.imagecatalog.services.services;

import si.fri.rso.samples.imagecatalog.lib.Session;
import si.fri.rso.samples.imagecatalog.lib.User;
import si.fri.rso.samples.imagecatalog.models.entities.SessionEntity;
import si.fri.rso.samples.imagecatalog.models.responses.SessionResponse;
import si.fri.rso.samples.imagecatalog.services.beans.SessionBean;
import si.fri.rso.samples.imagecatalog.services.beans.UsersBean;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionService {
    @Inject
    private SessionBean sessionBean;

    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    public static LocalDateTime generateSessionValidUntil() {
        return LocalDateTime.now().plusHours(1);
    }

    public static boolean checkSessionId() {
        return true;
    }


    public static SessionEntity createSessionResponse(User user) {
        // create new session
        Session session = new Session(user.getUserId());

        SessionResponse sessionEntity = new SessionResponse(user.getUserId(), user.getEmail(), session.getId(), session.getValidUntil());

        return new SessionEntity();
    }
}
