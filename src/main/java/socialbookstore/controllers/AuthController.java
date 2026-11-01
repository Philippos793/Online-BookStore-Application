package socialbookstore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import socialbookstore.domainmodel.User;
import socialbookstore.service.UserProfileService;
import socialbookstore.service.UserService;

@Controller
public class AuthController {

    private final UserService userService;
    private final UserProfileService userProfileService;

    public AuthController(UserService userService, UserProfileService userProfileService) {
        this.userService = userService;
        this.userProfileService = userProfileService;
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        addRegistrationOptions(model);
        return "auth/signup";
    }

    @PostMapping("/save")
    public String registerUser(@ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes) {
        if (userService.isUserPresent(user)) {
            model.addAttribute("errorMessage", "User already registered!");
            addRegistrationOptions(model);
            return "auth/signup";
        }

        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "User registered successfully! You can now log in.");
        return "redirect:/login";
    }

    private void addRegistrationOptions(Model model) {
        model.addAttribute("allCategories", userProfileService.findAllCategories());
        model.addAttribute("allAuthors", userProfileService.findAllAuthors());
    }
}
