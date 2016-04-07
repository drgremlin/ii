package org.ayfaar.app.spring.authentication;


import org.ayfaar.app.model.CurrentUser;
import org.ayfaar.app.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class AuthenticationTocken extends UsernamePasswordAuthenticationToken {
	private final User user;

	public AuthenticationTocken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, User user) {
		super(principal, credentials, authorities);
		this.user = user;
	}

	public User getUser() {
		return user;
	}
}
