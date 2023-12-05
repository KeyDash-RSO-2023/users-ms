package si.fri.rso.samples.imagecatalog.models.converters;

import si.fri.rso.samples.imagecatalog.lib.User;
import si.fri.rso.samples.imagecatalog.models.entities.UserEntity;

public class UsersConverter {

    public static User toDto(UserEntity entity) {

        User dto = new User();
        dto.setUserId(entity.getId());
        dto.setCreated(entity.getCreated());
        dto.setSurname(entity.getSurname());
        dto.setName(entity.getName());
        dto.setAge(entity.getAge());
        dto.setEmail(entity.getEmail());

        return dto;

    }

    public static UserEntity toEntity(User dto) {

        UserEntity entity = new UserEntity();
        entity.setCreated(dto.getCreated());
        entity.setSurname(dto.getSurname());
        entity.setName(dto.getName());
        entity.setAge(dto.getAge());
        entity.setEmail(dto.getEmail());

        return entity;

    }

}
