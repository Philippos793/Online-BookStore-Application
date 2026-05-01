package myy803.springboot.sb_tutorial_7_signup_signin.mappers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.Book;

public interface BookMapper extends JpaRepository<Book, Integer> {
	public List<Book> findByTitle(String title);
	public List<Book> findByTitleContaining(String title);
}
