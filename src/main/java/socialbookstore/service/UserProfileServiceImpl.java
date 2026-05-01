package socialbookstore.service;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;


import jakarta.transaction.Transactional;
import socialbookstore.domainmodel.Book;
import socialbookstore.domainmodel.BookAuthor;
import socialbookstore.domainmodel.BookCategory;
import socialbookstore.domainmodel.User;
import socialbookstore.domainmodel.UserProfile;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.mappers.BookAuthorMapper;
import socialbookstore.mappers.BookCategoryMapper;
import socialbookstore.mappers.BookMapper;
import socialbookstore.mappers.UserDAO;
import socialbookstore.mappers.UserProfileMapper;

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
    private ExactSearchStrategy exactSearchStrategy;
    @Autowired
    private AsyncEmailService asyncEmailService;
    @Autowired
    private ApproximateSearchStrategy approximateSearchStrategy;
    
    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);



    @Override
    public UserProfile retrieveProfile(String username) {
        // Retrieve and return user profile data
        return userProfileMapper.findByUsername(username);
    }
    @Override
    public void save(UserProfile userProfile) {
        // Save user profile data
        userProfileMapper.save(userProfile);
    }
    public List<BookCategory> findAllCategories() {
        return bookCategoriesMapper.findAll();
    }
   
    public List<BookAuthor> findAllAuthors() {
        return bookAuthorMapper.findAll();
    }
    @Override
    @Transactional
    public void addBookOffer(String username, Book book) {
        logger.info("Adding book for user: {}", username);
        UserProfile userProfile = userProfileMapper.findByUsername(username);
        if (userProfile == null) {
            throw new IllegalArgumentException("No user found with username: " + username);
        }

        book.setUserProfile(userProfile);
        logger.info("Setting user profile for book: {}", userProfile.getUsername());
        List<BookAuthor> managedAuthors = new ArrayList<>();
        for (BookAuthor author : book.getBookAuthors()) {
            List<BookAuthor> authors = bookAuthorMapper.findByName(author.getName());
            BookAuthor managedAuthor;
            if (authors.isEmpty()) {
                managedAuthor = bookAuthorMapper.save(author);
            } else {
                managedAuthor = authors.get(0);
            }
            managedAuthors.add(managedAuthor);
        }
        book.setBookAuthors(managedAuthors);

        userProfile.getBookOffers().add(book);

        userProfileMapper.save(userProfile); // Save the updated user profile
        bookMapper.save(book); // Save the book

        logger.info("Book saved successfully for user: {}", username);
    }
    @Override
    public List<Book> retrieveBookOffers(String username) {
        UserProfile userProfile = userProfileMapper.findByUsername(username);
        if (userProfile != null) {
            List<Book> books = userProfile.getBookOffers();
            for (Book book : books) {
                Hibernate.initialize(book.getBookAuthors()); // Ensure authors are loaded
                Hibernate.initialize(book.getBookCategory()); // Ensure category is loaded

            }
            return books;
        }
        return Collections.emptyList();    }
    
    
    
    public Optional<BookCategory> findCategoryById(Integer categoryId) {
        return bookCategoriesMapper.findById(categoryId);
    }
    
    @Override
    @Transactional
    public void deleteBookOffer(String username, int bookId) {
        logger.info("Deleting book with ID: {} for user: {}", bookId, username);

        UserProfile userProfile = userProfileMapper.findByUsername(username);
        if (userProfile != null) {
            List<Book> bookOffers = userProfile.getBookOffers();
            logger.debug("User {} has {} book offers before deletion.", username, bookOffers.size());

            Book bookToRemove = null;
            for (Book book : bookOffers) {
                if (book.getBookId() == bookId) {
                    bookToRemove = book;
                    break;
                }
            }
            if (bookToRemove != null) {
                logger.debug("Found book with ID: {} for user: {}. Removing book.", bookId, username);

                bookOffers.remove(bookToRemove);
                userProfile.setBookOffers(bookOffers);
                bookToRemove.setUserProfile(null); // Clear the user profile reference from the book
                bookMapper.delete(bookToRemove); // Delete the book from the database
                userProfileMapper.save(userProfile); // Save the updated user profile

                logger.info("Book with ID: {} removed successfully for user: {}", bookId, username);

                logger.info("Book removed successfully for user: {}", username);
            } else {
                throw new IllegalArgumentException("Book not found in user's offers.");
            }
        } else {
            throw new IllegalArgumentException("No user found with username: " + username);
        }
    }
    @Override
    public ArrayList<Book> searchBooks(SearchFormData searchFormData) {
        SearchStrategy searchStrategy = determineSearchStrategy(searchFormData.isExactMatch());
        return searchStrategy.search(searchFormData, bookMapper);
    }

    private SearchStrategy determineSearchStrategy(boolean exactMatch) {
        return exactMatch ? exactSearchStrategy : approximateSearchStrategy;
    }

    
    @Override
    @Transactional
    public void requestBook(int bookId, String username) {
        Book book = bookMapper.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + bookId));
        UserProfile userProfile = userProfileMapper.findByUsername(username);
        if (userProfile != null) {
            book.getRequestingUsers().add(userProfile);
            bookMapper.save(book);

            // Send email notification
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userProfile.getEmail());
            message.setSubject("Book Request Submitted");
            message.setText("You have successfully requested the book: " + book.getTitle());
            asyncEmailService.sendEmail(message);
        }
    }



    @Override
    @Transactional
    public List<UserProfile> retrieveRequestingUsers(int bookId) {
        Book book = bookMapper.findById(bookId).orElse(null);
        if (book != null) {
            return book.getRequestingUsers().stream().toList();
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional
    public List<Book> retrieveBookRequests(String username) {
        UserProfile userProfile = userProfileMapper.findByUsername(username);
        if (userProfile != null) {

            List<Book> requestedBooks = userProfile.getRequestedBooks();
            for (Book book : requestedBooks) {
            	System.out.println("mphka\n");

                Hibernate.initialize(book.getBookAuthors()); // Ensure authors are loaded
                Hibernate.initialize(book.getBookCategory()); // Ensure category is loaded
                logger.info("Requested book: Title - {}, Summary - {}, Category - {}, Authors - {}",
                        book.getTitle(),
                        book.getSummary(),
                        book.getBookCategory().getName(),
                        book.getBookAuthors().stream().map(BookAuthor::getName).collect(Collectors.joining(", ")));
            }
            return requestedBooks;
        }
        logger.info("No user profile found for username: {}", username);
        return Collections.emptyList();
    }
    @Transactional
    @Override
    public void acceptRequest(int bookId, String username) {
        Book book = bookMapper.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + bookId));
        UserProfile acceptedUser = userProfileMapper.findByUsername(username);
        if (acceptedUser != null) {
            List<UserProfile> requestingUsers = book.getRequestingUsers();
            for (UserProfile user : requestingUsers) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                if (user.getUsername().equals(username)) {
                    // Notify user of acceptance
                    message.setSubject("Book Request Accepted");
                    message.setText("Your request for the book " + book.getTitle() + " has been accepted.");
                } else {
                    // Notify other users of rejection
                    message.setSubject("Book Request Rejected");
                    message.setText("Your request for the book " + book.getTitle() + " has been rejected.");
                }
                asyncEmailService.sendEmail(message);
            }
            // Clear the requests as the book is given to the accepted user
            book.setRequestingUsers(Collections.emptyList());
            bookMapper.save(book);
        }
    }

}