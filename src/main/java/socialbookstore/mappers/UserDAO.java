package socialbookstore.mappers;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import socialbookstore.domainmodel.User;

public interface UserDAO extends JpaRepository<User, Integer> {
	
	Optional<User> findByUsername(String username);
}
