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
 * The type Review dao.
 */
@Repository
public class ReviewDao extends BaseDao<Review> {
    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param filters  the filters
     * @return the page
     */
    public Page<Review> findAll(Pageable pageable, Map<String, Object> filters) {
        Objects.requireNonNull(pageable);
        Objects.requireNonNull(filters);

        try {
            List<Review> result = ((TypedQuery<Review>) createFindAllQuery(pageable, filters, false)).getResultList();
            Long count = ((TypedQuery<Long>) createFindAllQuery(pageable, filters, true)).getSingleResult();

            return new PageImpl<>(result, pageable, count);
        } catch (RuntimeException e) {
            throw new PersistenceException(e);
        }
    }

    /**
     * Find by contract and author list.
     *
     * @param contract the contract
     * @param author   the author
     * @return the list
     */
    public List<Review> findByContractAndAuthor(Contract contract, Consumer author) {
        Objects.requireNonNull(contract);
        Objects.requireNonNull(author);

        try {
            return em.createNamedQuery("Review.findByContractAndAuthor", Review.class)
                    .setParameter("contract", contract)
                    .setParameter("author", author)
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
            cq = cb.createQuery(Review.class);
        }
        Root<Review> review = cq.from(Review.class);

        // Filters
        List<Predicate> predicates = new ArrayList<>();

        ParameterExpression<Enum> status = null;
        if (filters.containsKey("status")) {
            status = cb.parameter(Enum.class);
            predicates.add(cb.equal(review.get(Review_.status), status));
        }

        ParameterExpression<Consumer> author = null;
        if (filters.containsKey("authorId")) {
            author = cb.parameter(Consumer.class);
            predicates.add(cb.equal(review.get(Review_.author), author));
        }

        ParameterExpression<Consumer> reviewed = null;
        if (filters.containsKey("reviewedId")) {
            reviewed = cb.parameter(Consumer.class);

            Predicate tenantOrOwner = cb.or(
                    cb.equal(review.get(Review_.contract).get(Contract_.tenant), reviewed),
                    cb.equal(review.get(Review_.contract).get(Contract_.property).get(Property_.owner), reviewed));
            Predicate notEqualPredicate = cb.notEqual(review.get(Review_.author), reviewed);

            predicates.add(tenantOrOwner);
            predicates.add(notEqualPredicate);
        }

        ParameterExpression<Contract> contract = null;
        if (filters.containsKey("contractId")) {
            contract = cb.parameter(Contract.class);
            predicates.add(cb.equal(review.get(Review_.contract), contract));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<?> query;

        if (count) {
            cq.select(cb.count(review));
            query = em.createQuery(cq);
        } else {
            cq.orderBy(QueryUtils.toOrders(pageable.getSort(), review, cb));
            cq.select(review);

            query = em.createQuery(cq)
                    .setMaxResults(pageable.getPageSize())
                    .setFirstResult((int) pageable.getOffset());
        }

        // Setting up parameters
        if (status != null) {
            query.setParameter(status, PublicationStatus.valueOf(String.valueOf(filters.get("status"))));
        }

        // Setting up parameters
        if (author != null) {
            query.setParameter(author, em.getReference(Consumer.class, filters.get("authorId")));
        }

        // Setting up parameters
        if (reviewed != null) {
            query.setParameter(reviewed, em.getReference(Consumer.class, filters.get("reviewedId")));
        }

        // Setting up parameters
        if (contract != null) {
            query.setParameter(contract, em.getReference(Contract.class, filters.get("contractId")));
        }

        return query;
    }
}
