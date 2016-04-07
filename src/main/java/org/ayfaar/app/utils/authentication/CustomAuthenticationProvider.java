package org.ayfaar.app.utils.authentication;


import org.ayfaar.app.model.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import java.util.Collection;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = (User)authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(user.getRole().toString());
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

}
