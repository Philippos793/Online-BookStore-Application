package socialbookstore.mappers;

import org.springframework.data.jpa.repository.JpaRepository;

import socialbookstore.domainmodel.BookCategory;

import java.util.List;

public interface BookCategoryMapper extends JpaRepository<BookCategory, Integer> {
    List<BookCategory> findByName(String name);
    List<BookCategory> findAll();
}
