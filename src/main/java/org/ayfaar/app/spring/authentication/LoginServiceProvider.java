package org.ayfaar.app.spring.authentication;

import org.ayfaar.app.dao.UserDao;
import org.ayfaar.app.model.User;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static java.util.Arrays.asList;

@RestController
@RequestMapping("api/auth")//+ LoginServiceConst.SERVICE_PATH)
public class LoginServiceProvider {
    @Inject
    UserDao userDao;
    public Authentication authenticate(String email, String passwordHash) {
        User user = userDao.getUserByEmail(email);

        final HttpServletRequest request = (HttpServletRequest) RequestContextHolder.currentRequestAttributes().resolveReference("request");

        Collection<? extends GrantedAuthority> authorities = asList(new SimpleGrantedAuthority("USER"));
        final AuthenticationTocken token = new AuthenticationTocken(email, passwordHash, authorities, user);
        return token;
    }

    @RequestMapping//(LoginServiceConst.CURENT_USER)
    public User getCurrentUser() {
        return CurrentUserArgumentResolver.getCurrentUser();
    }
}
