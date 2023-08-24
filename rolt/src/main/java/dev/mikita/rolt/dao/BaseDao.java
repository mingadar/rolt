package dev.mikita.rolt.dao;

import dev.mikita.rolt.exception.PersistenceException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * The type Base dao.
 *
 * @param <T> the type parameter
 */
public abstract class BaseDao<T> implements GenericDao<T> {
    /**
     * The Em.
     */
    @PersistenceContext
    protected EntityManager em;
    private final Class<T> type;

    /**
     * Instantiates a new Base dao.
     */
    @SuppressWarnings("unchecked")
    public BaseDao() {
        Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        type = (Class) pt.getActualTypeArguments()[0];
    }

    /**
     * Finds by id.
     * @param id Identifier
     * @return found entity
     */
    @Override
    public T find(Integer id) {
        Objects.requireNonNull(id);
        return em.find(type, id);
    }

    /**
     * Finds all.
     * @return found entity
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        try {
            return em.createQuery("Select t from " + type.getSimpleName() + " t").getResultList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Persists entity.
     * @param entity Entity to persist
     */
    @Override
    public void persist(T entity) {
        Objects.requireNonNull(entity);
        try {
            em.persist(entity);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Persists entities.
     * @param entities Entities to persist
     */
    @Override
    public void persist(Collection<T> entities) {
        Objects.requireNonNull(entities);
        if (entities.isEmpty()) {
            return;
        }

        try {
            entities.forEach(em::persist);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Updates entity.
     * @param entity Entity to update
     * @return updated entity
     */
    @Override
    public T update(T entity) {
        Objects.requireNonNull(entity);
        try {
            return em.merge(entity);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Removes entity.
     * @param entity Entity to remove
     */
    @Override
    public void remove(T entity) {
        Objects.requireNonNull(entity);
        try {
            em.remove(em.contains(entity) ? entity : em.merge(entity));
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Checks if entity exists.
     * @param id Entity identifier
     * @return
     */
    @Override
    public boolean exists(Integer id) {
        return em.find(type, id) != null;
    }
}

