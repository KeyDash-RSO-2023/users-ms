package si.fri.rso.samples.imagecatalog.models.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public class SessionResponse {
    public SessionResponse(Integer userId, String email, String sessionId, LocalDateTime validUntil) {
        this.userId = userId;
        this.email = email;
        this.sessionId = sessionId;
        this.validUntil = validUntil;
    }

    private Integer userId;

    private String email;

    private String sessionId;

    private LocalDateTime validUntil;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
}
