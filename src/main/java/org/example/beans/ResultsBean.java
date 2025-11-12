package org.example.beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Query;
import org.example.entities.ResultEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("resultsBean")
@SessionScoped
public class ResultsBean implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private EntityManagerFactory emf;
    private EntityManager em;
    private List<ResultEntity> results;
    
    @PostConstruct
    public void init() {
        emf = Persistence.createEntityManagerFactory("web3PU");
        em = emf.createEntityManager();
        loadResults();
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
    
    public void addResult(ResultEntity result) {
        try {
            em.getTransaction().begin();
            em.persist(result);
            em.getTransaction().commit();
            loadResults();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error saving result", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadResults() {
        try {
            Query query = em.createQuery("SELECT r FROM ResultEntity r ORDER BY r.timestamp DESC");
            results = query.getResultList();
        } catch (Exception e) {
            results = new ArrayList<>();
        }
    }
    
    public List<ResultEntity> getResults() {
        if (results == null) {
            loadResults();
        }
        return results;
    }
    
    public void clearResults() {
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM ResultEntity");
            query.executeUpdate();
            em.getTransaction().commit();
            loadResults();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Error clearing results", e);
        }
    }
}

