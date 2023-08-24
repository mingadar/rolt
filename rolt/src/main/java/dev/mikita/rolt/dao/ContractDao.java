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
import java.time.LocalDate;
import java.util.*;

/**
 * The type Contract dao.
 */
@Repository
public class ContractDao extends BaseDao<Contract> {
    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param filters  the filters
     * @return the page
     */
    public Page<Contract> findAll(Pageable pageable, Map<String, Object> filters) {
        Objects.requireNonNull(pageable);

        try {
            List<Contract> result = ((TypedQuery<Contract>) createFindAllQuery(pageable, filters, false)).getResultList();
            Long count = ((TypedQuery<Long>) createFindAllQuery(pageable, filters, true)).getSingleResult();

            return new PageImpl<>(result, pageable, count);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Find intersections by date range list.
     *
     * @param property the property
     * @param start    the start
     * @param end      the end
     * @return the list
     */
    public List<Contract> findIntersectionsByDateRange(Property property, LocalDate start, LocalDate end) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(start);
        Objects.requireNonNull(end);
        try {
            return em.createNamedQuery("Contract.findIntersectionsByDateRange", Contract.class)
                    .setParameter("property", property)
                    .setParameter("start", start)
                    .setParameter("end", end)
                    .getResultList();
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
            cq = cb.createQuery(Contract.class);
        }
        Root<Contract> contract = cq.from(Contract.class);

        // Filters
        List<Predicate> predicates = new ArrayList<>();

        ParameterExpression<Landlord> landlord = null;
        if (filters.containsKey("landlordId")) {
            landlord = cb.parameter(Landlord.class);
            predicates.add(cb.equal(contract.get(Contract_.property).get(Property_.owner), landlord));
        }

        ParameterExpression<Tenant> tenant = null;
        if (filters.containsKey("tenantId")) {
            tenant = cb.parameter(Tenant.class);
            predicates.add(cb.equal(contract.get(Contract_.tenant), tenant));
        }

        ParameterExpression<Property> property = null;
        if (filters.containsKey("propertyId")) {
            property = cb.parameter(Property.class);
            predicates.add(cb.equal(contract.get(Contract_.property), property));
        }

        ParameterExpression<LocalDate> fromDate = null;
        if (filters.containsKey("fromDate")) {
            fromDate = cb.parameter(LocalDate.class);
            predicates.add(cb.greaterThanOrEqualTo(contract.get(Contract_.startDate), fromDate));
        }

        ParameterExpression<LocalDate> toDate = null;
        if (filters.containsKey("toDate")) {
            toDate = cb.parameter(LocalDate.class);
            predicates.add(cb.lessThanOrEqualTo(contract.get(Contract_.endDate), toDate));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<Property> query;

        if (count) {
            cq.select(cb.count(contract));
            query = em.createQuery(cq);
        } else {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), contract, cb));
            cq.select(contract);

            query = em.createQuery(cq)
                    .setMaxResults(pageable.getPageSize())
                    .setFirstResult((int) pageable.getOffset());
        }

        // Setting up parameters
        if (landlord != null) {
            query.setParameter(landlord, em.getReference(Landlord.class, filters.get("landlordId")));
        }

        if (tenant != null) {
            query.setParameter(tenant, em.getReference(Tenant.class, filters.get("tenantId")));
        }

        if (property != null) {
            query.setParameter(property, em.getReference(Property.class, filters.get("propertyId")));
        }

        if (fromDate != null) {
            query.setParameter(fromDate, (LocalDate) filters.get("fromDate"));
        }

        if (toDate != null) {
            query.setParameter(toDate, (LocalDate) filters.get("toDate"));
        }

        return query;
    }
}
