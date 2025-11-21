package org.example.repository;

import java.util.ArrayList;
import java.util.List;

import org.example.entities.ResultEntity;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;

/**
 * Repository слой - отвечает только за работу с базой данных.
 * Не является managed bean для JSF, но является CDI бином.
 */
@ApplicationScoped
public class ResultRepository {
    
    private EntityManagerFactory emf;
    private EntityManager em;
    
    @PostConstruct
    public void init() {
        emf = Persistence.createEntityManagerFactory("web3PU");
        em = emf.createEntityManager();
    }
    
    @PreDestroy
    public void destroy() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    public void save(ResultEntity entity) {
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error saving result", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<ResultEntity> findAll() {
        try {
            Query query = em.createQuery("SELECT r FROM ResultEntity r ORDER BY r.timestamp DESC");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public void deleteAll() {
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM ResultEntity");
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error clearing results", e);
        }
    }
}

