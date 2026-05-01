package myy803.springboot.sb_tutorial_7_signup_signin.service;

import java.util.List;

import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.Book;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.UserProfile;

public interface UserProfileService {
	 UserProfile retrieveProfile(String username);
	 void addBookForUser(String username, Book book);
    
/*
 * void save(UserProfile userProfileFormData);
    List<Book> retrieveBookOffers(String username);

   

  //  List<Book> searchBooks(SearchFormData searchFormData);

  //  List<Book> recommendBooks(String username, RecommendationsFormData recommendationsFormData);

    void requestBook(int bookId, String username);

    List<UserProfile> retrieveRequestingUsers(int bookId);

    void deleteBookOffer(String username, int bookId);

    void deleteBookRequest(String username, int bookId);*/
}
