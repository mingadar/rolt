package dev.mikita.rolt.service;

import dev.mikita.rolt.dao.TenantDao;
import dev.mikita.rolt.dao.UserDao;
import dev.mikita.rolt.entity.ConsumerStatus;
import dev.mikita.rolt.entity.Property;
import dev.mikita.rolt.entity.Role;
import dev.mikita.rolt.entity.Tenant;
import dev.mikita.rolt.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The type Tenant service.
 */
@Service
public class TenantService {
    private final TenantDao tenantDao;
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    /**
     * Instantiates a new Tenant service.
     *
     * @param tenantDao       the tenant dao
     * @param passwordEncoder the password encoder
     * @param userDao         the user dao
     */
    @Autowired
    public TenantService(TenantDao tenantDao,
                         PasswordEncoder passwordEncoder,
                         UserDao userDao) {
        this.tenantDao = tenantDao;
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
    }

    /**
     * Find all page.
     *
     * @param pageable the pageable
     * @param filters  the filters
     * @return the page
     */
    @Transactional(readOnly = true)
    public Page<Tenant> findAll(Pageable pageable, Map<String, Object> filters) {
        return tenantDao.findAll(pageable, filters);
    }

    /**
     * Find tenant.
     *
     * @param id the id
     * @return the tenant
     */
    @Transactional(readOnly = true)
    public Tenant find(Integer id) {
        return tenantDao.find(id);
    }

    /**
     * Persist.
     *
     * @param user the user
     */
    @Transactional
    public void persist(Tenant user) {
        Objects.requireNonNull(user);
        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new ValidationException("A user with this email already exists.");
        }

        user.setRole(Role.TENANT);
        user.encodePassword(passwordEncoder);
        tenantDao.persist(user);
    }

    /**
     * Update.
     *
     * @param user the user
     */
    @Transactional
    public void update(Tenant user) {
        tenantDao.update(user);
    }

    /**
     * Remove.
     *
     * @param user the user
     */
    @Transactional
    public void remove(Tenant user) {
        Objects.requireNonNull(user);
        user.setInSearch(false);
        user.setStatus(ConsumerStatus.DELETED);
        tenantDao.update(user);
    }

    /**
     * Gets favorites.
     *
     * @param user the user
     * @return the favorites
     */
    @Transactional
    public Set<Property> getFavorites(Tenant user) {
        Objects.requireNonNull(user);
        return user.getFavorites();
    }

    /**
     * Add favorite.
     *
     * @param property the property
     * @param user     the user
     */
    @Transactional
    public void addFavorite(Property property, Tenant user) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(user);
        user.addFavorite(property);
        tenantDao.update(user);
    }

    /**
     * Remove favorite.
     *
     * @param property the property
     * @param user     the user
     */
    @Transactional
    public void removeFavorite(Property property, Tenant user) {
        Objects.requireNonNull(property);
        Objects.requireNonNull(user);
        user.removeFavorite(property);
        tenantDao.update(user);
    }

    /**
     * Block.
     *
     * @param user the user
     */
    @Transactional
    public void block(Tenant user) {
        Objects.requireNonNull(user);
        user.setInSearch(false);
        user.setStatus(ConsumerStatus.BANNED);
        tenantDao.update(user);
    }

    /**
     * Active.
     *
     * @param user the user
     */
    @Transactional
    public void active(Tenant user) {
        Objects.requireNonNull(user);
        user.setStatus(ConsumerStatus.ACTIVE);
        tenantDao.update(user);
    }
}
