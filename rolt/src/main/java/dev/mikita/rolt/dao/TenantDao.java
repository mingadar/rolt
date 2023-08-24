package dev.mikita.rolt.dao;

import dev.mikita.rolt.entity.ConsumerGender;
import dev.mikita.rolt.entity.ConsumerStatus;
import dev.mikita.rolt.entity.Tenant;
import dev.mikita.rolt.entity.Tenant_;
import dev.mikita.rolt.exception.PersistenceException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Tenant dao.
 */
@Repository
public class TenantDao extends BaseDao<Tenant> {
    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param filters  the filters
     * @return the page
     */
    public Page<Tenant> findAll(Pageable pageable, Map<String, Object> filters) {
        Objects.requireNonNull(pageable);
        Objects.requireNonNull(filters);

        try {
            List<Tenant> result = ((TypedQuery<Tenant>) createFindAllQuery(pageable, filters, false)).getResultList();
            Long count = ((TypedQuery<Long>) createFindAllQuery(pageable, filters, true)).getSingleResult();

            return new PageImpl<>(result, pageable, count);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Find all in search list.
     *
     * @return the list
     */
    public List<Tenant> findAllInSearch() {
        try {
            return em.createQuery("SELECT t FROM Tenant t WHERE t.status = dev.mikita.rolt.entity.ConsumerStatus.ACTIVE AND t.inSearch = true", Tenant.class).getResultList();
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Creates a findAll query.
     * @param pageable pageable
     * @param filters filters
     * @param count count
     * @return query
     */
    private TypedQuery<?> createFindAllQuery(Pageable pageable, Map<String, Object> filters, boolean count) {
        Objects.requireNonNull(pageable);
        Objects.requireNonNull(filters);

        // Main Query
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq;
        if (count) {
            cq = cb.createQuery(Long.class);
        } else {
            cq = cb.createQuery(Tenant.class);
        }
        Root<Tenant> tenant = cq.from(Tenant.class);

        // Filters
        List<Predicate> predicates = new ArrayList<>();

        ParameterExpression<Boolean> inSearch = null;
        if (filters.containsKey("inSearch")) {
            inSearch = cb.parameter(Boolean.class);
            predicates.add(cb.equal(tenant.get(Tenant_.inSearch), inSearch));
        }

        ParameterExpression<Enum> status = null;
        if (filters.containsKey("status")) {
            status = cb.parameter(Enum.class);
            predicates.add(cb.equal(tenant.get(Tenant_.status), status));
        }

        ParameterExpression<Enum> gender = null;
        if (filters.containsKey("gender")) {
            gender = cb.parameter(Enum.class);
            predicates.add(cb.equal(tenant.get(Tenant_.gender), gender));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<?> query;

        if (count) {
            cq.select(cb.count(tenant));
            query = em.createQuery(cq);
        } else {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), tenant, cb));
            cq.select(tenant);

            query = em.createQuery(cq)
                    .setMaxResults(pageable.getPageSize())
                    .setFirstResult((int) pageable.getOffset());
        }

        // Setting up parameters
        if (inSearch != null) {
            query.setParameter(inSearch, (Boolean) filters.get("inSearch"));
        }

        if (status != null) {
            query.setParameter(status, ConsumerStatus.valueOf(String.valueOf(filters.get("status"))));
        }

        if (gender != null) {
            query.setParameter(gender, ConsumerGender.valueOf(String.valueOf(filters.get("gender"))));
        }

        return query;
    }

}
