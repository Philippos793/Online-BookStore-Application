package socialbookstore.mappers;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import socialbookstore.domainmodel.BookAuthor;

public interface BookAuthorMapper extends JpaRepository<BookAuthor, Long> {
    List<BookAuthor> findByName(String name);
    List<BookAuthor> findAll();
}
