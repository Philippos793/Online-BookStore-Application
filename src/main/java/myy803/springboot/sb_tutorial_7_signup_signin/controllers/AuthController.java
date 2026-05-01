package myy803.springboot.sb_tutorial_7_signup_signin.controllers;

import java.util.Optional;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import myy803.springboot.sb_tutorial_7_signup_signin.service.UserService;
import myy803.springboot.sb_tutorial_7_signup_signin.domainmodel.User;
import myy803.springboot.sb_tutorial_7_signup_signin.mappers.UserDAO;
import myy803.springboot.sb_tutorial_7_signup_signin.service.UserProfileServiceImpl;
@Controller
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    private UserProfileServiceImpl userProfileService;  // Service to handle user profiles and related entities

    @Autowired
    private UserDAO userdao; 
   
    
    @RequestMapping("/login")
    public String login(){
        return "auth/signin";
    }

   

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
    	 
        model.addAttribute("user", new User());  // Assuming the User model includes an empty UserProfile
       
        
        model.addAttribute("allCategories", userProfileService.findAllCategories());  // Fetch categories
        model.addAttribute("allAuthors", userProfileService.findAllAuthors());  // This method needs to be implemented
        return "auth/signup";
    }


    
    @PostMapping("/save")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        if(userService.isUserPresent(user)) {
            model.addAttribute("errorMessage", "User already registered!");
            return "auth/signup";
        }
        
        user.getUserProfile().setUsername(user.getUsername()); // Ensure username is set
        userService.saveUser(user);
        model.addAttribute("successMessage", "User registered successfully!");
        return "redirect:/signin";
    }

}
