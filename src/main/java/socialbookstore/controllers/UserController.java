package socialbookstore.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import socialbookstore.domainmodel.Book;
import socialbookstore.domainmodel.BookAuthor;
import socialbookstore.domainmodel.BookCategory;
import socialbookstore.formsdata.SearchFormData;
import socialbookstore.service.UserProfileService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/dashboard")
    public String getUserDashboard(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Book> books = userProfileService.retrieveBookOffers(username);
        int pendingRequests = books.stream().mapToInt(book -> book.getRequestingUsers().size()).sum();

        model.addAttribute("profile", userProfileService.retrieveProfile(username));
        model.addAttribute("books", books);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("myRequestCount", userProfileService.retrieveBookRequests(username).size());
        model.addAttribute("communityBooks", userProfileService.retrieveRecentBooksOfOthers(username));
        return "user/dashboard";
    }

    @GetMapping("/addBookOffer")
    public String showAddBookOfferForm(Model model) {
        model.addAttribute("authors", userProfileService.findAllAuthors());
        model.addAttribute("categories", userProfileService.findAllCategories());
        model.addAttribute("book", new Book());
        return "user/add_book_offer";
    }

    @PostMapping("/addBook")
    public String addBook(@ModelAttribute("book") Book book,
                          @RequestParam("authorNames") List<String> authorNames,
                          @RequestParam("categoryId") Integer categoryId,
                          RedirectAttributes redirectAttributes,
                          Authentication authentication) {
        Optional<BookCategory> category = userProfileService.findCategoryById(categoryId);
        if (category.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid category selected!");
            return "redirect:/user/addBookOffer";
        }
        book.setBookCategory(category.get());

        // The service resolves these names to existing authors or creates new ones
        List<BookAuthor> authors = authorNames.stream()
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .map(name -> {
                    BookAuthor author = new BookAuthor();
                    author.setName(name);
                    return author;
                })
                .toList();
        book.setBookAuthors(authors);

        userProfileService.addBookOffer(authentication.getName(), book);
        redirectAttributes.addFlashAttribute("successMessage", "Book added successfully!");
        return "redirect:/user/myBooks";
    }

    @GetMapping("/myBooks")
    public String listUserBooks(Model model, Authentication authentication) {
        model.addAttribute("books", userProfileService.retrieveBookOffers(authentication.getName()));
        return "user/users_book";
    }

    @PostMapping("/removeBook")
    public String removeBook(@RequestParam("bookId") int bookId,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            userProfileService.deleteBookOffer(authentication.getName(), bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Book removed successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing book: " + e.getMessage());
        }
        return "redirect:/user/myBooks";
    }

    // Without a search, the page shows the most recently added books
    @GetMapping("/searchBooks")
    public String showSearchBooksForm(Model model) {
        model.addAttribute("searchData", new SearchFormData());
        model.addAttribute("books", userProfileService.retrieveRecentBooks());
        model.addAttribute("searched", false);
        return "user/search_books";
    }

    @PostMapping("/searchBooks")
    public String searchBooks(@ModelAttribute("searchData") SearchFormData searchFormData, Model model) {
        model.addAttribute("books", userProfileService.searchBooks(searchFormData));
        model.addAttribute("searched", true);
        return "user/search_books";
    }

    @PostMapping("/requestBook")
    public String requestBook(@RequestParam("bookId") int bookId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            userProfileService.requestBook(bookId, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Book request sent successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error requesting book: " + e.getMessage());
        }
        return "redirect:/user/searchBooks";
    }

    @GetMapping("/bookRequests")
    public String showUserBookRequests(Model model, Authentication authentication) {
        model.addAttribute("bookRequests", userProfileService.retrieveBookRequests(authentication.getName()));
        model.addAttribute("suggestedBooks", userProfileService.retrieveRecentBooksOfOthers(authentication.getName()));
        return "user/book_requests";
    }

    @GetMapping("/requestingUsers")
    public String showRequestingUsersForBookOffer(@RequestParam("bookId") int bookId,
                                                  Model model,
                                                  Authentication authentication) {
        model.addAttribute("book", userProfileService.retrieveOwnedBook(bookId, authentication.getName()));
        model.addAttribute("requestingUsers",
                userProfileService.retrieveRequestingUsers(bookId, authentication.getName()));
        model.addAttribute("bookId", bookId);
        return "user/requesting_users";
    }

    @PostMapping("/acceptRequest")
    public String acceptRequest(@RequestParam("bookId") int bookId,
                                @RequestParam("username") String username,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            userProfileService.acceptRequest(bookId, authentication.getName(), username);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Request accepted successfully and all users notified!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/user/requestingUsers?bookId=" + bookId;
    }
}
