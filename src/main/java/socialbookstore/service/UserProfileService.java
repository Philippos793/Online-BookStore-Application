package socialbookstore.service;

import java.util.List;

import socialbookstore.domainmodel.Book;
import socialbookstore.domainmodel.UserProfile;
import socialbookstore.formsdata.SearchFormData;

public interface UserProfileService {
	 UserProfile retrieveProfile(String username);
	 void save(UserProfile userProfile);
	 List<Book> retrieveBookOffers(String username);
	 void addBookOffer(String username, Book book);
	 List<Book> searchBooks(SearchFormData searchFormData);
	 void requestBook(int bookId, String username);
	 List<Book> retrieveBookRequests(String username);
	 List<UserProfile> retrieveRequestingUsers(int bookId);
	 void deleteBookOffer(String username, int bookId);
	 void acceptRequest( int bookId,String username);



/*
 * void save(UserProfile userProfileFormData);

   

  //  

  //  List<Book> recommendBooks(String username, RecommendationsFormData recommendationsFormData);

   


    void deleteBookRequest(String username, int bookId);*/
}
