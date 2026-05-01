package myy803.springboot.sb_tutorial_7_signup_signin.mappers;

import org.springframework.data.jpa.repository.JpaRepository;

import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.BookCategory;

import java.util.List;

public interface BookCategoryMapper extends JpaRepository<BookCategory, Integer> {
    List<BookCategory> findByName(String name);
    List<BookCategory> findAll();
}
