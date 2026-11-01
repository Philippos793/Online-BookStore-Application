package socialbookstore.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import socialbookstore.domainmodel.Role;
import socialbookstore.domainmodel.User;
import socialbookstore.domainmodel.UserProfile;
import socialbookstore.mappers.UserDAO;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserDAO userDAO;

	public UserServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder, UserDAO userDAO) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userDAO = userDAO;
	}

	@Override
	public void saveUser(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		if (user.getUserProfile() == null) {
			user.setUserProfile(new UserProfile());
		}
		if (user.getRole() == null) {
			user.setRole(Role.USER);
		}
		user.getUserProfile().setUsername(user.getUsername());
		user.getUserProfile().setUser(user); // bidirectional linkage

		userDAO.save(user);
	}

	@Override
	public boolean isUserPresent(User user) {
		return userDAO.findByUsername(user.getUsername()).isPresent();
	}

	// Method defined in Spring Security UserDetailsService interface
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userDAO.findByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("USER_NOT_FOUND %s", username)));
	}
}
