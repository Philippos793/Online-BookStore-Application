package socialbookstore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import socialbookstore.service.UserProfileService;

@Controller
public class HomeController {

    private final UserProfileService userProfileService;

    public HomeController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("recentBooks", userProfileService.retrieveRecentBooks());
        model.addAttribute("bookCount", userProfileService.countBooks());
        model.addAttribute("memberCount", userProfileService.countMembers());
        model.addAttribute("categoryCount", userProfileService.findAllCategories().size());
        return "homepage";
    }
}
