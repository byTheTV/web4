package org.example.repository;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.example.entities.ResultEntity;

import java.util.ArrayList;
import java.util.List;

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
    
    /**
     * Сохраняет результат в БД.
     */
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
    
    /**
     * Получает все результаты из БД, отсортированные по времени.
     */
    @SuppressWarnings("unchecked")
    public List<ResultEntity> findAll() {
        try {
            Query query = em.createQuery("SELECT r FROM ResultEntity r ORDER BY r.timestamp DESC");
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Удаляет все результаты из БД.
     */
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

