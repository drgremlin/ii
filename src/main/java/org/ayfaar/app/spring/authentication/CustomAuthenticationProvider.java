package org.ayfaar.app.spring.authentication;

import org.ayfaar.app.model.CurrentUser;
import org.ayfaar.app.services.user.CurrentUserDetailsService;
import org.ayfaar.app.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;


public class CustomAuthenticationProvider implements AuthenticationProvider {

    private LoginServiceProvider externalServiceAuthenticator;

    public CustomAuthenticationProvider(LoginServiceProvider externalServiceAuthenticator) {
        this.externalServiceAuthenticator = externalServiceAuthenticator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new BadCredentialsException("Invalid Domain User Credentials");
        }

        return externalServiceAuthenticator.authenticate(username, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
