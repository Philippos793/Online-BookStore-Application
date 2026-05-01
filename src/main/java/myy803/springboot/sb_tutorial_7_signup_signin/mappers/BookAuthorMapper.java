package myy803.springboot.sb_tutorial_7_signup_signin.mappers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.BookAuthor;

public interface BookAuthorMapper extends JpaRepository<BookAuthor, Long> {
    List<BookAuthor> findByName(String name);
    List<BookAuthor> findAll();
}
