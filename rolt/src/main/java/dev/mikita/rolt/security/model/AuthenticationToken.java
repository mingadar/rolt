package dev.mikita.rolt.security.model;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.security.Principal;
import java.util.Collection;

/**
 * The type Authentication token.
 */
public class AuthenticationToken extends AbstractAuthenticationToken implements Principal {
    private CustomUserDetails userDetails;

    /**
     * Instantiates a new Authentication token.
     *
     * @param authorities the authorities
     * @param userDetails the user details
     */
    public AuthenticationToken(Collection<? extends GrantedAuthority> authorities, CustomUserDetails userDetails) {
        super(authorities);
        this.userDetails = userDetails;
        super.setAuthenticated(true);
        super.setDetails(userDetails);
    }

    /**
     * Returns credentials.
     * @return the credentials
     */
    @Override
    public String getCredentials() {
        return userDetails.getPassword();
    }

    /**
     * Returns principal.
     * @return the principal
     */
    @Override
    public CustomUserDetails getPrincipal() {
        return userDetails;
    }
}
