package socialbookstore.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import socialbookstore.domainmodel.Role;

/*
 * Determines the url that is appropriate for the logged user based on their role
 */
@Component
public class CustomSecuritySuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Override
	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		if (response.isCommitted()) return;
		getRedirectStrategy().sendRedirect(request, response, determineTargetUrl(authentication));
	}

	protected String determineTargetUrl(Authentication authentication) {
		boolean isAdmin = authentication.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals(Role.ADMIN.name()));
		return isAdmin ? "/admin/dashboard" : "/user/dashboard";
	}
}
