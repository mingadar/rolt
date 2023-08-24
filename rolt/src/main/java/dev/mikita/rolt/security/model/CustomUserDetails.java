package dev.mikita.rolt.security.model;

import dev.mikita.rolt.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.*;

/**
 * The type Custom user details.
 */
public class CustomUserDetails implements UserDetails {
    private User user;
    private final Set<GrantedAuthority> authorities;

    /**
     * Instantiates a new Custom user details.
     *
     * @param user the user
     */
    public CustomUserDetails(User user) {
        Objects.requireNonNull(user);
        this.user = user;
        this.authorities = new HashSet<>();
        addUserRole();
    }

    /**
     * Instantiates a new Custom user details.
     *
     * @param user        the user
     * @param authorities the authorities
     */
    public CustomUserDetails(User user, Collection<GrantedAuthority> authorities) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(authorities);
        this.user = user;
        this.authorities = new HashSet<>();
        addUserRole();
        this.authorities.addAll(authorities);
    }

    /**
     * Adds a user role.
     */
    private void addUserRole() {
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
    }

    /**
     * Returns authorities.
     * @return the authorities
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableCollection(authorities);
    }

    /**
     * Returns password.
     * @return the password
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns username.
     * @return the username
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Checks if account is not expired.
     * @return bool
     */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    /**
     * Checks if account is not locked.
     * @return bool
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Checks if credentials are not expired.
     * @return bool
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Checks if is enabled.
     * @return bool
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * Erase credentials.
     */
    public void eraseCredentials() {
        user.erasePassword();
    }
}
