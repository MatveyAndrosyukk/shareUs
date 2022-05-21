package api.sweater.controller;

import api.sweater.model.User;
import api.sweater.repository.UserRepository;
import api.sweater.service.SendMailService;
import api.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public String getProfile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("edit/username")
    public String getEditUsernamePage() {
        return "edit-username";
    }

    @GetMapping("edit/email")
    public String getEditEmailPage() {
        return "edit-email";
    }

    @GetMapping("edit/password")
    public String getEditPasswordPage() {
        return "edit-password";
    }

    @PostMapping("edit/edit-username")
    public String editUsername(@AuthenticationPrincipal User user,
                               @RequestParam("username") String username,
                               Model model) {
        boolean isChanged = userService.editUsername(user, username);
        if (!isChanged) {
            model.addAttribute("usernameMessage", "This username exists!");
            return "edit-username";
        }
        return "redirect:/profile";
    }

    @PostMapping("edit/edit-email")
    public String editEmail(@AuthenticationPrincipal User user,
                            @RequestParam("email") String email,
                            Model model) {
        userService.editEmail(user, email);
        model.addAttribute("emailMessage", "Check your email for the changes to take effect");
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("activate-email/{email}/{code}")
    public String activateEmail(@AuthenticationPrincipal User user,
                                @PathVariable("email") String email,
                                @PathVariable("code") String code) {
        userService.activateEmail(user, email, code);
        return "redirect:/profile";
    }

    @PostMapping("edit/edit-password")
    public String editPassword(@AuthenticationPrincipal User user,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("repNewPassword") String repNewPassword,
                               Model model) {
        if (userService.updatePassword(user, newPassword, repNewPassword)){
            return "redirect:/profile";
        }
        model.addAttribute("repNewPasswordError", "Passwords don't match");
        return "edit-password";
    }
}
