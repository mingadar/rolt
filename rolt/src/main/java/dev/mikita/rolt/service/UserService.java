package dev.mikita.rolt.service;

import dev.mikita.rolt.dao.UserDao;
import dev.mikita.rolt.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * The type User service.
 */
@Service
public class UserService {
    private final UserDao userDao;

    /**
     * Instantiates a new User service.
     *
     * @param userDao the user dao
     */
    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Find all list.
     *
     * @return the list
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userDao.findAll();
    }

    /**
     * Find user.
     *
     * @param id the id
     * @return the user
     */
    @Transactional(readOnly = true)
    public User find(Integer id) {
        return userDao.find(id);
    }

    /**
     * Persist.
     *
     * @param user the user
     */
    @Transactional
    public void persist(User user) {
        userDao.persist(user);
    }

    /**
     * Update.
     *
     * @param user the user
     */
    @Transactional
    public void update(User user) {
        userDao.update(user);
    }

    /**
     * Remove.
     *
     * @param user the user
     */
    @Transactional
    public void remove(User user) {
        userDao.remove(user);
    }

    /**
     * Exists boolean.
     *
     * @param email the email
     * @return the boolean
     */
    @Transactional(readOnly = true)
    public boolean exists(String email) {
        return userDao.findByEmail(email) != null;
    }
}
