package si.fri.rso.samples.imagecatalog.models.converters;

import si.fri.rso.samples.imagecatalog.lib.Session;
import si.fri.rso.samples.imagecatalog.models.entities.SessionEntity;

public class SessionConverter {

    public static Session toDto(SessionEntity entity) {

        Session dto = new Session();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setValidUntil(entity.getValidUntil());

        return dto;
    }

    public static SessionEntity toEntity(Session dto) {
        SessionEntity entity = new SessionEntity();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setValidUntil(dto.getValidUntil());

        return entity;
    }
}
