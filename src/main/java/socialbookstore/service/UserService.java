package socialbookstore.service;

import socialbookstore.domainmodel.User;

public interface UserService {
	void saveUser(User user);
	boolean isUserPresent(User user);
}
