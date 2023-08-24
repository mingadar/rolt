package dev.mikita.rolt.service;

import dev.mikita.rolt.dao.ReviewDao;
import dev.mikita.rolt.entity.PublicationStatus;
import dev.mikita.rolt.entity.Review;
import dev.mikita.rolt.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Review service.
 */
@Service
public class ReviewService {
    private final ReviewDao reviewDao;

    /**
     * Instantiates a new Review service.
     *
     * @param reviewDao the review dao
     */
    @Autowired
    public ReviewService(ReviewDao reviewDao) {
        this.reviewDao = reviewDao;
    }

    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param filters  the filters
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<Review> findAll(Pageable pageable, Map<String, Object> filters) {
        return reviewDao.findAll(pageable, filters);
    }

    /**
     * Find review.
     *
     * @param id the id
     * @return the review
     */
    @Transactional(readOnly = true)
    public Review find(Integer id) {
        return reviewDao.find(id);
    }

    /**
     * Persist.
     *
     * @param review the review
     */
    @Transactional
    public void persist(Review review) {
        List<Review> reviews = reviewDao.findByContractAndAuthor(review.getContract(), review.getAuthor());

        if (!reviews.isEmpty()) {
            throw new ValidationException("You have already left feedback for this contract.");
        }

        reviewDao.persist(review);
    }

    /**
     * Update.
     *
     * @param review the review
     */
    @Transactional
    public void update(Review review) {
        Objects.requireNonNull(review);
        reviewDao.update(review);
    }

    /**
     * Remove.
     *
     * @param review the review
     */
    @Transactional
    public void remove(Review review) {
        Objects.requireNonNull(review);
        review.setStatus(PublicationStatus.DELETED);
        reviewDao.update(review);
    }

    /**
     * Publish.
     *
     * @param review the review
     */
    @Transactional
    public void publish(Review review) {
        Objects.requireNonNull(review);
        review.setStatus(PublicationStatus.PUBLISHED);
        reviewDao.update(review);
    }

    /**
     * Moderate.
     *
     * @param review the review
     */
    @Transactional
    public void moderate(Review review) {
        Objects.requireNonNull(review);
        review.setStatus(PublicationStatus.MODERATION);
        reviewDao.update(review);
    }
}
