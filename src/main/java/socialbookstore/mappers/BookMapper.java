package socialbookstore.mappers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import socialbookstore.domainmodel.Book;

public interface BookMapper extends JpaRepository<Book, Integer> {
	List<Book> findByTitle(String title);
	List<Book> findByTitleContaining(String title);
	List<Book> findTop8ByOrderByBookIdDesc();
	List<Book> findTop6ByUserProfile_UsernameNotOrderByBookIdDesc(String username);
}
