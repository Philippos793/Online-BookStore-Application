package myy803.springboot.sb_tutorial_7_signup_signin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import myy803.springboot.sb_tutorial_7_signup_signin.*;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.User;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.UserProfile;
import myy803.springboot.sb_tutorial_7_signup_signin.mappers.BookAuthorMapper;
import myy803.springboot.sb_tutorial_7_signup_signin.mappers.BookCategoryMapper;
import myy803.springboot.sb_tutorial_7_signup_signin.mappers.BookMapper;
import myy803.springboot.sb_tutorial_7_signup_signin.mappers.UserProfileMapper;
import myy803.springboot.sb_tutorial_7_signup_signin.mappers.UserDAO;

import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.Book;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.BookAuthor;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.BookCategory;
@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Autowired
    private BookAuthorMapper bookAuthorMapper;

    @Autowired
    private BookCategoryMapper bookCategoriesMapper;

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private UserDAO userdao;
   // @Autowired
    //private SearchFactory searchFactory;

   // @Autowired
   // private RecommendationsFactory recommendationsFactory;

    @Override
    public UserProfile retrieveProfile(String username) {
        // Retrieve and return user profile data
        return userProfileMapper.findByUsername(username);
    }

    public void save(UserProfile userProfile) {
        // Save user profile data
        userProfileMapper.save(userProfile);
    }
    public List<BookCategory> findAllCategories() {
        return bookCategoriesMapper.findAll();
    }

    public List<BookAuthor> findAllAuthors() {
        // Implement this method if not already implemented
        return bookAuthorMapper.findAll();
    }
    @Override
    public void addBookForUser(String username, Book book) {
        // Optionally fetch user details if needed for contextual actions or validations
        Optional<User> user = userdao.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("No user found with username: " + username);
        }

        // Add book to the repository, without linking it directly to the user in the database
        bookMapper.save(book);
    }

    public List<Book> getBooksByUser(String username) {
        // This would ideally filter books based on some user interaction stored elsewhere
        // Since Book does not directly link to User in the DB, this needs to be handled contextually
        return bookMapper.findAll();  // Placeholder: Adjust based on actual logic
    }
  
/*	
    @Override
    public List<Book> retrieveBookOffers(String username) {
        // Retrieve book offers for a user
        return bookMapper.findBooksByUser(username);
    }

      @Override
    public void addBookOffer(String username, Book book) {
       Add a book offer to a user's profile
        bookMapper.addBook(book);
    }

    @Override
    public List<Book> searchBooks(Search searchFormData) {
        // Implement search logic
        return searchFactory.searchBooks(searchFormData);
    }

    @Override
    public List<BookFormData> recommendBooks(String username, RecommendationsFormData recommendationsFormData) {
        // Implement recommendation logic
        return recommendationsFactory.recommendBooks(username, recommendationsFormData);
    }

    @Override
    public void requestBook(int bookId, String username) {
        // Logic to request a book
        bookMapper.requestBook(bookId, username);
    }

    @Override
    public List<UserProfileFormData> retrieveRequestingUsers(int bookId) {
        // Retrieve users requesting a specific book
        return userProfileMapper.findRequestingUsers(bookId);
    }

    @Override
    public void deleteBookOffer(String username, int bookId) {
        // Delete a book offer
        bookMapper.deleteBookOffer(username, bookId);
    }

    @Override
    public void deleteBookRequest(String username, int bookId) {
        // Delete a book request
        bookMapper.deleteBookRequest(username, bookId);
    }

	@Override
	public void save(UserProfileFormData userProfileFormData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBookOffer(String username, BookFormData bookFormData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<BookFormData> searchBooks(SearchFormData searchFormData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BookFormData> recommendBooks(String username, RecommendationsFormData recommendationsFormData) {
		// TODO Auto-generated method stub
		return null;
	}
*/
}
