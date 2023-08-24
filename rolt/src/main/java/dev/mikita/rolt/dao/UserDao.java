package dev.mikita.rolt.dao;

import dev.mikita.rolt.entity.User;
import org.springframework.stereotype.Repository;
import java.util.Objects;

/**
 * The type User dao.
 */
@Repository
public class UserDao extends BaseDao<User> {
    /**
     * Find by email user.
     *
     * @param email the email
     * @return the user
     */
    public User findByEmail(String email) {
        Objects.requireNonNull(email);
        try {
            return em.createNamedQuery("User.findByEmail", User.class).setParameter("email", email)
                    .getSingleResult();
        } catch (RuntimeException e) {
            return null;
        }
    }
}
