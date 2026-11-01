package socialbookstore.service;

import java.util.List;
import java.util.Optional;

import socialbookstore.domainmodel.Book;
import socialbookstore.domainmodel.BookAuthor;
import socialbookstore.domainmodel.BookCategory;
import socialbookstore.domainmodel.UserProfile;
import socialbookstore.formsdata.SearchFormData;

public interface UserProfileService {
	UserProfile retrieveProfile(String username);
	void save(UserProfile userProfile);

	List<BookCategory> findAllCategories();
	List<BookAuthor> findAllAuthors();
	Optional<BookCategory> findCategoryById(Integer categoryId);

	List<Book> retrieveBookOffers(String username);
	void addBookOffer(String username, Book book);
	void deleteBookOffer(String username, int bookId);

	List<Book> searchBooks(SearchFormData searchFormData);

	void requestBook(int bookId, String username);
	List<Book> retrieveBookRequests(String username);
	List<UserProfile> retrieveRequestingUsers(int bookId, String ownerUsername);
	void acceptRequest(int bookId, String ownerUsername, String acceptedUsername);
}
