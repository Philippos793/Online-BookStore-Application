package socialbookstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import socialbookstore.domainmodel.Role;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}

	/*
	 * DaoAuthenticationProvider authenticates a username and password
	 * using our UserDetailsService (UserServiceImpl) and the BCrypt encoder.
	 */
	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
			BCryptPasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);
		return authProvider;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
			CustomSecuritySuccessHandler customSecuritySuccessHandler,
			DaoAuthenticationProvider authenticationProvider) throws Exception {
		http
			.authorizeHttpRequests(authz -> authz
				.requestMatchers("/", "/login", "/register", "/save", "/css/**").permitAll()
				.requestMatchers("/admin/**").hasAuthority(Role.ADMIN.name())
				.requestMatchers("/user/**").hasAuthority(Role.USER.name())
				.anyRequest().authenticated()
			)
			.formLogin(fL -> fL
				.loginPage("/login")
				.failureUrl("/login?error=true")
				.successHandler(customSecuritySuccessHandler)
				.usernameParameter("username")
				.passwordParameter("password")
			)
			.logout(logOut -> logOut
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/")
			)
			.authenticationProvider(authenticationProvider);

		return http.build();
	}
}
