package socialbookstore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import socialbookstore.service.UserProfileService;

@Controller
public class AdminController {

    private final UserProfileService userProfileService;

    public AdminController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/admin/dashboard")
    public String getAdminHome(Model model) {
        model.addAttribute("bookCount", userProfileService.countBooks());
        model.addAttribute("memberCount", userProfileService.countMembers());
        model.addAttribute("authorCount", userProfileService.findAllAuthors().size());
        model.addAttribute("categoryCount", userProfileService.findAllCategories().size());
        model.addAttribute("recentBooks", userProfileService.retrieveRecentBooks());
        return "admin/dashboard";
    }
}
