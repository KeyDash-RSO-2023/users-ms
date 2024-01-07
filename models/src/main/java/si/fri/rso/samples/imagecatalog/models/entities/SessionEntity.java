package si.fri.rso.samples.imagecatalog.models.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@NamedQueries(value =
        {
                @NamedQuery(name = "SessionEntity.getAll",
                        query = "SELECT im FROM SessionEntity im"),
                @NamedQuery(name = "SessionEntity.findByUserId",
                        query = "SELECT im FROM SessionEntity im WHERE im.userId = " +
                                ":userId"),
        })
public class SessionEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name="userId")
    private Integer userId;

    @Column(name="validUntil")
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
