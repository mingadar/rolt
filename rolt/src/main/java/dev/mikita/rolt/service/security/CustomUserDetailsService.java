package dev.mikita.rolt.service.security;

import dev.mikita.rolt.dao.UserDao;
import dev.mikita.rolt.entity.User;
import dev.mikita.rolt.security.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * The type Custom user details service.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserDao userDao;

    /**
     * Instantiates a new Custom user details service.
     *
     * @param userDao the user dao
     */
    @Autowired
    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Loads by username.
     * @param email the email
     * @return the user details
     * @throws UsernameNotFoundException the exception
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userDao.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User with email " + email + " not found.");
        }
        return new CustomUserDetails(user);
    }
}
