package si.fri.rso.samples.imagecatalog.lib;

import java.time.LocalDateTime;
import java.util.UUID;

public class Session {
    private String id;

    private Integer userId;

    private LocalDateTime validUntil;

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

}
