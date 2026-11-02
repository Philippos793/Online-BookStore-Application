package socialbookstore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import socialbookstore.domainmodel.Book;
import socialbookstore.domainmodel.BookAuthor;
import socialbookstore.domainmodel.BookCategory;
import socialbookstore.domainmodel.UserProfile;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.mappers.BookAuthorMapper;
import socialbookstore.mappers.BookCategoryMapper;
import socialbookstore.mappers.BookMapper;
import socialbookstore.mappers.UserProfileMapper;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileMapper userProfileMapper;
    private final BookAuthorMapper bookAuthorMapper;
    private final BookCategoryMapper bookCategoryMapper;
    private final BookMapper bookMapper;
    private final ExactSearchStrategy exactSearchStrategy;
    private final ApproximateSearchStrategy approximateSearchStrategy;
    private final AsyncEmailService asyncEmailService;

    public UserProfileServiceImpl(UserProfileMapper userProfileMapper, BookAuthorMapper bookAuthorMapper,
            BookCategoryMapper bookCategoryMapper, BookMapper bookMapper, ExactSearchStrategy exactSearchStrategy,
            ApproximateSearchStrategy approximateSearchStrategy, AsyncEmailService asyncEmailService) {
        this.userProfileMapper = userProfileMapper;
        this.bookAuthorMapper = bookAuthorMapper;
        this.bookCategoryMapper = bookCategoryMapper;
        this.bookMapper = bookMapper;
        this.exactSearchStrategy = exactSearchStrategy;
        this.approximateSearchStrategy = approximateSearchStrategy;
        this.asyncEmailService = asyncEmailService;
    }

    @Override
    public UserProfile retrieveProfile(String username) {
        return userProfileMapper.findByUsername(username);
    }

    @Override
    public void save(UserProfile userProfile) {
        userProfileMapper.save(userProfile);
    }

    @Override
    public List<BookCategory> findAllCategories() {
        return bookCategoryMapper.findAll();
    }

    @Override
    public List<BookAuthor> findAllAuthors() {
        return bookAuthorMapper.findAll();
    }

    @Override
    public Optional<BookCategory> findCategoryById(Integer categoryId) {
        return bookCategoryMapper.findById(categoryId);
    }

    @Override
    @Transactional
    public void addBookOffer(String username, Book book) {
        UserProfile userProfile = requireProfile(username);
        book.setUserProfile(userProfile);

        // Reuse existing authors by name, create the missing ones
        List<BookAuthor> managedAuthors = new ArrayList<>();
        for (BookAuthor author : book.getBookAuthors()) {
            List<BookAuthor> existing = bookAuthorMapper.findByName(author.getName());
            managedAuthors.add(existing.isEmpty() ? bookAuthorMapper.save(author) : existing.get(0));
        }
        book.setBookAuthors(managedAuthors);

        userProfile.getBookOffers().add(book);
        bookMapper.save(book);
        logger.info("Book '{}' added for user: {}", book.getTitle(), username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> retrieveBookOffers(String username) {
        UserProfile userProfile = userProfileMapper.findByUsername(username);
        if (userProfile == null) {
            return Collections.emptyList();
        }
        List<Book> books = userProfile.getBookOffers();
        books.forEach(book -> Hibernate.initialize(book.getBookAuthors()));
        return books;
    }

    @Override
    @Transactional
    public void deleteBookOffer(String username, int bookId) {
        Book book = findOwnedBook(bookId, username);
        book.getUserProfile().getBookOffers().remove(book);
        book.getRequestingUsers().clear();
        bookMapper.delete(book);
        logger.info("Book with ID: {} removed for user: {}", bookId, username);
    }

    @Override
    public List<Book> searchBooks(SearchFormData searchFormData) {
        SearchStrategy searchStrategy = searchFormData.isExactMatch() ? exactSearchStrategy : approximateSearchStrategy;
        return searchStrategy.search(searchFormData);
    }

    @Override
    public List<Book> retrieveRecentBooks() {
        return bookMapper.findTop8ByOrderByBookIdDesc();
    }

    @Override
    public List<Book> retrieveRecentBooksOfOthers(String username) {
        return bookMapper.findTop6ByUserProfile_UsernameNotOrderByBookIdDesc(username);
    }

    @Override
    public long countBooks() {
        return bookMapper.count();
    }

    @Override
    public long countMembers() {
        return userProfileMapper.count();
    }

    @Override
    @Transactional
    public void requestBook(int bookId, String username) {
        Book book = findBook(bookId);
        UserProfile userProfile = requireProfile(username);
        if (book.getUserProfile().getUsername().equals(username)) {
            throw new IllegalArgumentException("You cannot request your own book.");
        }
        if (book.getRequestingUsers().contains(userProfile)) {
            throw new IllegalArgumentException("You have already requested this book.");
        }
        book.getRequestingUsers().add(userProfile);
        bookMapper.save(book);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userProfile.getEmail());
        message.setSubject("Book Request Submitted");
        message.setText("You have successfully requested the book: " + book.getTitle());
        asyncEmailService.sendEmail(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> retrieveBookRequests(String username) {
        UserProfile userProfile = userProfileMapper.findByUsername(username);
        if (userProfile == null) {
            return Collections.emptyList();
        }
        List<Book> requestedBooks = userProfile.getRequestedBooks();
        requestedBooks.forEach(book -> Hibernate.initialize(book.getBookAuthors()));
        return requestedBooks;
    }

    @Override
    @Transactional(readOnly = true)
    public Book retrieveOwnedBook(int bookId, String ownerUsername) {
        return findOwnedBook(bookId, ownerUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfile> retrieveRequestingUsers(int bookId, String ownerUsername) {
        return List.copyOf(findOwnedBook(bookId, ownerUsername).getRequestingUsers());
    }

    @Override
    @Transactional
    public void acceptRequest(int bookId, String ownerUsername, String acceptedUsername) {
        Book book = findOwnedBook(bookId, ownerUsername);
        List<UserProfile> requestingUsers = book.getRequestingUsers();
        boolean hasRequested = requestingUsers.stream()
                .anyMatch(user -> user.getUsername().equals(acceptedUsername));
        if (!hasRequested) {
            throw new IllegalArgumentException("User " + acceptedUsername + " has not requested this book.");
        }

        for (UserProfile user : requestingUsers) {
            boolean accepted = user.getUsername().equals(acceptedUsername);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject(accepted ? "Book Request Accepted" : "Book Request Rejected");
            message.setText("Your request for the book " + book.getTitle() + " has been "
                    + (accepted ? "accepted." : "rejected."));
            asyncEmailService.sendEmail(message);
        }

        // The book is given to the accepted user, so all pending requests are closed
        requestingUsers.clear();
        bookMapper.save(book);
    }

    private UserProfile requireProfile(String username) {
        UserProfile userProfile = userProfileMapper.findByUsername(username);
        if (userProfile == null) {
            throw new IllegalArgumentException("No user found with username: " + username);
        }
        return userProfile;
    }

    private Book findBook(int bookId) {
        return bookMapper.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book Id: " + bookId));
    }

    private Book findOwnedBook(int bookId, String username) {
        Book book = findBook(bookId);
        if (book.getUserProfile() == null || !book.getUserProfile().getUsername().equals(username)) {
            throw new AccessDeniedException("Book " + bookId + " does not belong to " + username);
        }
        return book;
    }
}
