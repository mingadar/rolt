package dev.mikita.rolt.dao;

import dev.mikita.rolt.entity.*;
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
 * The type Landlord dao.
 */
@Repository
public class LandlordDao extends BaseDao<Landlord> {
    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param filters  the filters
     * @return the page
     */
    public Page<Landlord> findAll(Pageable pageable, Map<String, Object> filters) {
        Objects.requireNonNull(pageable);
        Objects.requireNonNull(filters);

        try {
            List<Landlord> result = ((TypedQuery<Landlord>) createFindAllQuery(pageable, filters, false)).getResultList();
            Long count = ((TypedQuery<Long>) createFindAllQuery(pageable, filters, true)).getSingleResult();

            return new PageImpl<>(result, pageable, count);
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
            cq = cb.createQuery(Landlord.class);
        }
        Root<Landlord> landlord = cq.from(Landlord.class);

        // Filters
        List<Predicate> predicates = new ArrayList<>();

        ParameterExpression<Enum> status = null;
        if (filters.containsKey("status")) {
            status = cb.parameter(Enum.class);
            predicates.add(cb.equal(landlord.get(Landlord_.status), status));
        }

        ParameterExpression<Enum> gender = null;
        if (filters.containsKey("gender")) {
            gender = cb.parameter(Enum.class);
            predicates.add(cb.equal(landlord.get(Landlord_.gender), gender));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }


        TypedQuery<?> query;

        if (count) {
            cq.select(cb.count(landlord));
            query = em.createQuery(cq);
        } else {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), landlord, cb));
            cq.select(landlord);

            query = em.createQuery(cq)
                    .setMaxResults(pageable.getPageSize())
                    .setFirstResult((int) pageable.getOffset());
        }

        // Setting up parameters
        if (status != null) {
            query.setParameter(status, ConsumerStatus.valueOf(String.valueOf(filters.get("status"))));
        }

        if (gender != null) {
            query.setParameter(gender, ConsumerGender.valueOf(String.valueOf(filters.get("gender"))));
        }

        return query;
    }
}
