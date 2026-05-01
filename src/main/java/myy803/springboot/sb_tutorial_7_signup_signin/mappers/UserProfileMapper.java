package myy803.springboot.sb_tutorial_7_signup_signin.mappers;


import org.springframework.data.jpa.repository.JpaRepository;

import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.UserProfile;

import java.util.Optional;


public interface UserProfileMapper extends JpaRepository<UserProfile, String> {
    UserProfile  findByUsername(String username);
}