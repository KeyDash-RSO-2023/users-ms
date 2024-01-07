package si.fri.rso.samples.imagecatalog.services.beans;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.metrics.annotation.Timed;

import si.fri.rso.samples.imagecatalog.lib.User;
import si.fri.rso.samples.imagecatalog.models.converters.UsersConverter;
import si.fri.rso.samples.imagecatalog.models.entities.UserEntity;


@RequestScoped
public class UsersBean {

    private Logger log = Logger.getLogger(UsersBean.class.getName());

    @Inject
    private EntityManager em;

    public List<User> getUsers() {

        TypedQuery<UserEntity> query = em.createNamedQuery(
                "UsersEntity.getAll", UserEntity.class);

        List<UserEntity> resultList = query.getResultList();

        return resultList.stream().map(UsersConverter::toDto).collect(Collectors.toList());

    }

    @Timed
    public List<User> getUsersFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, UserEntity.class, queryParameters).stream()
                .map(UsersConverter::toDto).collect(Collectors.toList());
    }

    public User getUser(Integer id) {

        UserEntity userEntity = em.find(UserEntity.class, id);

        if (userEntity == null) {
            throw new NotFoundException();
        }

        User user = UsersConverter.toDto(userEntity);

        return user;
    }

    public User getUserByEmail(String email) {

        System.out.println(email);
        TypedQuery<UserEntity> query = em.createNamedQuery("UsersEntity.getByEmail", UserEntity.class);
        query.setParameter("email", email);

        System.out.println(query);
        List<UserEntity> users = query.getResultList();

        System.out.println(users);
        if (users.isEmpty()) {
            throw new NotFoundException("User with email " + email + " not found");
        }

        UserEntity userEntity = users.get(0);

        System.out.println(userEntity.getEmail());
        return UsersConverter.toDto(userEntity);
    }

    public User createUser(User user) {

        UserEntity userEntity = UsersConverter.toEntity(user);

        try {
            beginTx();
            em.persist(userEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        if (userEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return UsersConverter.toDto(userEntity);
    }

    public User putUser(Integer id, User user) {

        UserEntity c = em.find(UserEntity.class, id);

        if (c == null) {
            return null;
        }

        UserEntity updatedUserEntity = UsersConverter.toEntity(user);

        try {
            beginTx();
            updatedUserEntity.setId(c.getId());
            updatedUserEntity = em.merge(updatedUserEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return UsersConverter.toDto(updatedUserEntity);
    }

    public boolean deleteUser(Integer id) {

        UserEntity user = em.find(UserEntity.class, id);

        if (user != null) {
            try {
                beginTx();
                em.remove(user);
                commitTx();
            }
            catch (Exception e) {
                rollbackTx();
            }
        }
        else {
            return false;
        }

        return true;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    private void commitTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
