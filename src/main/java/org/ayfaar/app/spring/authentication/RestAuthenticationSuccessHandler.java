package org.ayfaar.app.spring.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	@Inject ObjectMapper mapper;
	//@Inject LoginAuditor auditor;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
		clearAuthenticationAttributes(request);
		if (authentication.isAuthenticated()) {
			response.getWriter().append(((AuthenticationTocken) authentication).getUser().getId().toString());
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
}
