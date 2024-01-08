package si.fri.rso.samples.imagecatalog.lib;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    // ATTRIBUTES
    private String id;

    private Integer userId;

    private LocalDateTime validUntil;

    // GETTERS AND SETTERS
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    // CONSTRUCTORS
    public Session() {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.validUntil = LocalDateTime.now().plusHours(1); // Set the session to expire in 1 hour
    }

    public Session(int userId) {
        this.id = UUID.randomUUID().toString(); // Generate a unique ID
        this.validUntil = LocalDateTime.now().plusHours(1); // Set the session to expire in 1 hour
        this.userId = userId;
    }
}
