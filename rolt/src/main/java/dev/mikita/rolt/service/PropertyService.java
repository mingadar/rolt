package dev.mikita.rolt.service;

import dev.mikita.rolt.dao.PropertyDao;
import dev.mikita.rolt.entity.Property;
import dev.mikita.rolt.entity.PublicationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Objects;

/**
 * The type Property service.
 */
@Service
public class PropertyService {
    private final PropertyDao propertyDao;

    /**
     * Instantiates a new Property service.
     *
     * @param propertyDao the property dao
     */
    @Autowired
    public PropertyService(PropertyDao propertyDao) {
        this.propertyDao = propertyDao;
    }

    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param filters  the filters
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<Property> findAll(Pageable pageable, Map<String, Object> filters) {
        return propertyDao.findAll(pageable, filters);
    }

    /**
     * Find property.
     *
     * @param id the id
     * @return the property
     */
    @Transactional(readOnly = true)
    public Property find(Integer id) {
        return propertyDao.find(id);
    }

    /**
     * Persist.
     *
     * @param property the property
     */
    @Transactional
    public void persist(Property property) {
        propertyDao.persist(property);
    }

    /**
     * Update.
     *
     * @param property the property
     */
    @Transactional
    public void update(Property property) {
        Objects.requireNonNull(property);
        propertyDao.update(property);
    }

    /**
     * Remove.
     *
     * @param property the property
     */
    @Transactional
    public void remove(Property property) {
        Objects.requireNonNull(property);
        property.setAvailable(false);
        property.setStatus(PublicationStatus.DELETED);
        propertyDao.update(property);
    }

    /**
     * Publish.
     *
     * @param property the property
     */
    @Transactional
    public void publish(Property property) {
        Objects.requireNonNull(property);
        property.setStatus(PublicationStatus.PUBLISHED);
        propertyDao.update(property);
    }

    /**
     * Moderate.
     *
     * @param property the property
     */
    @Transactional
    public void moderate(Property property) {
        Objects.requireNonNull(property);
        property.setStatus(PublicationStatus.MODERATION);
        propertyDao.update(property);
    }
}
