package socialbookstore.controllers;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import socialbookstore.domainmodel.Book;
import socialbookstore.domainmodel.BookAuthor;
import socialbookstore.domainmodel.BookCategory;
import socialbookstore.domainmodel.User;
import socialbookstore.domainmodel.UserProfile;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.service.UserProfileService;
import socialbookstore.service.UserProfileServiceImpl;
import socialbookstore.service.UserService;
import socialbookstore.service.UserServiceImpl;
import socialbookstore.mappers.BookAuthorMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller

public class UserController {
	

    @Autowired
    private UserProfileServiceImpl userProfileService;
    @Autowired
    private BookAuthorMapper bookAuthorMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @RequestMapping("/user/dashboard")
    public String getUserDashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Book> books = userProfileService.retrieveBookOffers(username);
        model.addAttribute("books", books);
        return "user/dashboard";
    }
    // Method to display the form for adding a new book
    @GetMapping("/user/addBookOffer")
    public String showAddBookOfferForm(Model model) {
        model.addAttribute("authors", userProfileService.findAllAuthors());  // Assume this fetches authors
        model.addAttribute("categories", userProfileService.findAllCategories());
        model.addAttribute("book", new Book());
        return "user/add_book_offer";
    }

    @PostMapping("/user/addBook")
    public String addBook(@ModelAttribute("book") Book book,
                          @RequestParam("authorNames") List<String> authorNames,
                          @RequestParam("categoryId") Integer categoryId,
                          RedirectAttributes redirectAttributes,
                          Authentication authentication) {
        Optional<BookCategory> category = userProfileService.findCategoryById(categoryId);
        if (!category.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid category selected!");
            return "redirect:/user/addBookOffer";
        }

        book.setBookCategory(category.get());

        List<BookAuthor> authors = new ArrayList<>();
        for (String authorName : authorNames) {
            List<BookAuthor> existingAuthors = bookAuthorMapper.findByName(authorName);
            BookAuthor author;
            if (existingAuthors.isEmpty()) {
                author = new BookAuthor();
                author.setName(authorName);
                author = bookAuthorMapper.save(author);
            } else {
                author = existingAuthors.get(0);
            }
            authors.add(author);
        }
        book.setBookAuthors(authors);

        String username = authentication.getName();
        userProfileService.addBookOffer(username, book);

        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
        return "redirect:/user/myBooks";
    }




    // Method to list all books associated with the user
   

    // Method to list all books associated with the user
    @GetMapping("/user/myBooks")
    public String listUserBooks(Model model, Authentication authentication) {
    	   String username = authentication.getName();
           List<Book> books = userProfileService.retrieveBookOffers(username);
           
           model.addAttribute("books", books);
           return "user/users_book"; // View that lists the books
    }
    @PostMapping("/user/removeBook")
    public String removeBook(@RequestParam("bookId") int bookId, Authentication authentication, RedirectAttributes redirectAttributes) {
    	String username = authentication.getName();
        try {
            userProfileService.deleteBookOffer(username, bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Book removed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing book: " + e.getMessage());
        }
        return "redirect:/user/myBooks";
    }
    @GetMapping("/user/searchBooks")
    public String showSearchBooksForm(Model model) {
        model.addAttribute("searchData", new SearchFormData());
        return "user/search_books";
    }


    @PostMapping("/user/searchBooks")
    public String searchBooks(@ModelAttribute("searchData") SearchFormData searchFormData,
                              Model model) {
        List<Book> books = userProfileService.searchBooks(searchFormData);
        model.addAttribute("books", books);
        return "user/search_results";
    }

    @PostMapping("/user/requestBook")
    public String requestBook(@RequestParam("bookId") int bookId, 
                              Authentication authentication, 
                              RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        try {
            userProfileService.requestBook(bookId, username);
            redirectAttributes.addFlashAttribute("successMessage", "Book request sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error requesting book: " + e.getMessage());
        }
        return "redirect:/user/searchBooks";
    }
    @GetMapping("/user/bookRequests")
    public String showUserBookRequests(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Book> bookRequests = userProfileService.retrieveBookRequests(username);
        
        // Add logging to check the retrieved book requests
        if (bookRequests.isEmpty()) {
            logger.info("No book requests found for user: {}", username);
        } else {
            for (Book book : bookRequests) {
                logger.info("Book request found: Title - {}, Summary - {}, Category - {}, Authors - {}",
                        book.getTitle(),
                        book.getSummary(),
                        book.getBookCategory().getName(),
                        book.getBookAuthors().stream().map(BookAuthor::getName).collect(Collectors.joining(", ")));
            }
        }
        
        model.addAttribute("bookRequests", bookRequests);
        return "user/book_requests";
    }

    @GetMapping("/user/requestingUsers")
    public String showRequestingUsersForBookOffer(@RequestParam("bookId") int bookId, Model model) {
    	System.out.println("mphka\n");

        logger.info("Showing requesting users for book ID: {}", bookId);
        List<UserProfile> requestingUsers = userProfileService.retrieveRequestingUsers(bookId);
        if (requestingUsers.isEmpty()) {
            logger.info("No users have requested this book.");
        } else {
            logger.info("Found {} requesting users.", requestingUsers.size());
        }
        model.addAttribute("requestingUsers", requestingUsers);
        model.addAttribute("bookId", bookId);  // Add bookId to the model

        return "user/requesting_users";
    }
    @GetMapping("/user/acceptRequest")
    public String acceptRequest(@RequestParam("bookId") int bookId, @RequestParam("username") String username, RedirectAttributes redirectAttributes) {
        userProfileService.acceptRequest(bookId, username);
        redirectAttributes.addFlashAttribute("successMessage", "Request accepted successfully and all users notified!");
        return "redirect:/user/requestingUsers?bookId=" + bookId;
    }

    
 
    
}
