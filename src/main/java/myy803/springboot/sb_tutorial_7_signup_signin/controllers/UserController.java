package myy803.springboot.sb_tutorial_7_signup_signin.controllers;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import myy803.springboot.sb_tutorial_7_signup_signin.service.UserService;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.User;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.UserProfile;
import myy803.springboot.sb_tutorial_7_signup_signin.service.UserProfileService;
import myy803.springboot.sb_tutorial_7_signup_signin.service.UserProfileServiceImpl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.Book;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.BookAuthor;

import org.springframework.security.core.Authentication;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller

public class UserController {
	@Autowired
    private UserService userService;

    @Autowired
    private UserProfileServiceImpl userProfileService;
    
    @RequestMapping("/user/dashboard")
    public String getUserHome(){
//    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		 String currentPrincipalName = authentication.getName();
//		 System.err.println(currentPrincipalName);
		
        return "user/dashboard";
    }
    // Method to display the form for adding a new book
    @GetMapping("/user/addBookOffer")
    public String showAddBookOfferForm(Model model) {
    	List<BookAuthor> authors = userProfileService.findAllAuthors();  // Fetch all authors from the database
        model.addAttribute("authors", authors);
        model.addAttribute("book", new Book()); // Ensure the model attribute 'book' is added for the form
        return "user/add_book_offer"; // Return the view name of the add book offer HTML form
    }

    // Method to handle the submission of a new book
    @PostMapping("/user/addBook")
    public String addUserBook(Book book, RedirectAttributes redirectAttributes, Authentication authentication) {
        String username = authentication.getName();
        userProfileService.addBookForUser(username, book);
        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
        return "redirect:/user/myBooks"; // Redirect to the book listing page
    }

    // Method to list all books associated with the user
    @GetMapping("/user/myBooks")
    public String listUserBooks(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Book> books = userProfileService.getBooksByUser(username);
        if (books != null) {
            for (Book book : books) {
                // Optionally log to verify book authors are not null
                System.out.println("Book: " + book.getTitle() + " Authors: " + book.getBookAuthors());
            }
        }
        model.addAttribute("books", books);
        return "user/users_book";
    }

    
  //  @PostMapping("/books/addOffer")
  //  public String addBookOffer(@ModelAttribute Book bookData, @RequestParam("username") String username) {
   // 	userProfileService.saveOffer(bookData, username);
   //     return "redirect:/user/books/offers?username=" + username;
   // }
/*
   

    @GetMapping("/profile")
    public String viewProfile(@RequestParam("username") String username, Model model) {
        UserProfile userProfileData = userProfileService.retrieveProfile(username);
        model.addAttribute("profileData", userProfileData);
        return "user/profile";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@ModelAttribute UserProfile profileData, Model model) {
        userProfileService.saveProfile(profileData);
        model.addAttribute("message", "Profile Updated Successfully!");
        return "redirect:/user/profile?username=" + profileData.getUsername();
    }

    @GetMapping("/books/offers")
    public String viewBookOffers(@RequestParam("username") String username, Model model) {
        List<Book> bookOffers = userService.retrieveBookOffers(username);
        model.addAttribute("bookOffers", bookOffers);
        return "user/bookOffers";
    }

    

    @GetMapping("/books/requests")
    public String viewBookRequests(@RequestParam("username") String username, Model model) {
        List<BookFormData> bookRequests = userService.retrieveBookRequests(username);
        model.addAttribute("bookRequests", bookRequests);
        return "user/bookRequests";
    }

    @PostMapping("/books/request")
    public String requestBook(@RequestParam("bookId") Long bookId, @RequestParam("username") String username) {
        userService.requestBook(bookId, username);
        return "redirect:/user/books/requests?username=" + username;
    }

    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("searchData", new SearchFormData());
        return "user/search";
    }

    @PostMapping("/search")
    public String performSearch(@ModelAttribute SearchFormData searchData, Model model) {
        List<BookFormData> results = userService.searchBooks(searchData);
        model.addAttribute("searchResults", results);
        return "user/searchResults";
    }

    @GetMapping("/recommendations")
    public String recommendations(@RequestParam("username") String username, Model model) {
        RecommendationsFormData recommendations = userService.recommendBooks(username);
        model.addAttribute("recommendations", recommendations);
        return "user/recommendations";
    }
   */ 
    
}
