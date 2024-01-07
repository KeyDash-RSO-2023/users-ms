package si.fri.rso.samples.imagecatalog.services.beans;

import si.fri.rso.samples.imagecatalog.lib.Session;
import si.fri.rso.samples.imagecatalog.lib.User;
import si.fri.rso.samples.imagecatalog.models.converters.SessionConverter;
import si.fri.rso.samples.imagecatalog.models.converters.UsersConverter;
import si.fri.rso.samples.imagecatalog.models.entities.SessionEntity;
import si.fri.rso.samples.imagecatalog.models.entities.UserEntity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequestScoped

public class SessionBean {
    private Logger log = Logger.getLogger(UsersBean.class.getName());

    @Inject
    private EntityManager em;


    public List<Session> getSessions() {

        TypedQuery<SessionEntity> query = em.createNamedQuery(
                "SessionEntity.getAll", SessionEntity.class);

        List<SessionEntity> resultList = query.getResultList();

        return resultList.stream().map(SessionConverter::toDto).collect(Collectors.toList());

    }

    public Session getSession(Integer id) {

        SessionEntity sessionEntity = em.find(SessionEntity.class, id);

        if (sessionEntity == null) {
            throw new NotFoundException();
        }

        Session session = SessionConverter.toDto(sessionEntity);

        return session;
    }

    public Session createSession(Session session) {
        SessionEntity sessionEntity = SessionConverter.toEntity(session);

        try {
            beginTx();
            em.persist(sessionEntity);
            commitTx();
        }
        catch (Exception e) {
            System.out.println(e);
            rollbackTx();
        }

        if (sessionEntity.getId() == null) {
            throw new RuntimeException("Entity was not persisted");
        }

        return SessionConverter.toDto(sessionEntity);
    }

    public Session putSession(Integer id, Session session) {

        SessionEntity c = em.find(SessionEntity.class, id);

        if (c == null) {
            return null;
        }

        SessionEntity updateSessionEntity = SessionConverter.toEntity(session);

        try {
            beginTx();
            updateSessionEntity.setId(c.getId());
            updateSessionEntity = em.merge(updateSessionEntity);
            commitTx();
        }
        catch (Exception e) {
            rollbackTx();
        }

        return SessionConverter.toDto(updateSessionEntity);
    }

    public boolean deleteSession(String id) {

        System.out.println("Id " + id);
        SessionEntity session = em.find(SessionEntity.class, id);

        if (session != null) {
            try {
                beginTx();
                em.remove(session);
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

    public List<Session> getSessionsByUserId(int userId) {

        System.out.println(userId);
        TypedQuery<SessionEntity> query = em.createNamedQuery("SessionEntity.findByUserId", SessionEntity.class);
        query.setParameter("userId", userId);

        List<SessionEntity> sessions = query.getResultList();

        System.out.println(sessions);

        List<Session> sessionEntities = new ArrayList<>();

        for (SessionEntity session : sessions) {
            sessionEntities.add(SessionConverter.toDto(session));
        }

        return sessionEntities;
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
