package dev.mikita.rolt.service;

import dev.mikita.rolt.dao.ConsumerDao;
import dev.mikita.rolt.entity.Consumer;
import dev.mikita.rolt.entity.ConsumerStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

/**
 * The type Consumer service.
 */
@Service
public class ConsumerService {
    private final ConsumerDao consumerDao;

    /**
     * Instantiates a new Consumer service.
     *
     * @param consumerDao the consumer dao
     */
    @Autowired
    public ConsumerService(ConsumerDao consumerDao) {
        this.consumerDao = consumerDao;
    }

    /**
     * Find all list.
     *
     * @return the list
     */
    @Transactional(readOnly = true)
    public List<Consumer> findAll() {
        return consumerDao.findAll();
    }

    /**
     * Find consumer.
     *
     * @param id the id
     * @return the consumer
     */
    @Transactional(readOnly = true)
    public Consumer find(Integer id) {
        return consumerDao.find(id);
    }

    /**
     * Gets rating.
     *
     * @param consumer the consumer
     * @return the rating
     */
    @Transactional(readOnly = true)
    public Double getRating(Consumer consumer) {
        return consumerDao.getRating(consumer);
    }

    /**
     * Persist.
     *
     * @param city the city
     */
    @Transactional
    public void persist(Consumer city) {
        consumerDao.persist(city);
    }

    /**
     * Update.
     *
     * @param city the city
     */
    @Transactional
    public void update(Consumer city) {
        consumerDao.update(city);
    }

    /**
     * Remove.
     *
     * @param user the user
     */
    @Transactional
    public void remove(Consumer user) {
        Objects.requireNonNull(user);
        user.setStatus(ConsumerStatus.DELETED);
        consumerDao.update(user);
    }

    /**
     * Block.
     *
     * @param user the user
     */
    @Transactional
    public void block(Consumer user) {
        Objects.requireNonNull(user);
        user.setStatus(ConsumerStatus.BANNED);
        consumerDao.update(user);
    }

    /**
     * Active.
     *
     * @param user the user
     */
    @Transactional
    public void active(Consumer user) {
        Objects.requireNonNull(user);
        user.setStatus(ConsumerStatus.ACTIVE);
        consumerDao.update(user);
    }
}
