package api.sweater.controller;

import api.sweater.model.User;
import api.sweater.service.impl.UserServiceImpl;
import api.sweater.service.interfaces.UserService;
import api.sweater.service.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Value("${upload.path}")
    private String uploadPath;

    private final UserService userService;

    public ProfileController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getProfilePage(@AuthenticationPrincipal User currentUser, Model model) {
        User user = userService.findById(currentUser.getId());
        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("activate-email/{email}/{code}")
    public String activateEmail(@AuthenticationPrincipal User currentUser,
                                @PathVariable("email") String email,
                                @PathVariable("code") String code) {
        userService.activateEmail(currentUser, email, code);
        return "redirect:/profile";
    }


    @PostMapping("changeAvatar")
    public String changeAvatar(@AuthenticationPrincipal User currentUser,
                               @RequestParam("file") MultipartFile file) throws IOException {
        User user = userService.findById(currentUser.getId());
        FileUtils.deleteFile(user.getImageFilename(), uploadPath);
        userService.changeAvatar(file, currentUser);
        return "redirect:/profile";
    }

    @GetMapping("edit")
    public String getEditPage(@RequestParam() String e)
    {
        return switch (e) {
            case "username" -> "edit-username";
            case "email" -> "edit-email";
            case "password" -> "edit-password";
            default -> "redirect:/profile";
        };
    }


    @PostMapping("edit/edit-username")
    public String editUsername(@AuthenticationPrincipal User currentUser,
                               @RequestParam("username") String username,
                               Model model) {
        boolean isChanged = userService.editUsername(currentUser, username);
        if (!isChanged) {
            model.addAttribute("usernameMessage", "This username exists!");
            return "edit-username";
        }
        return "redirect:/profile";
    }

    @PostMapping("edit/edit-email")
    public String editEmail(@AuthenticationPrincipal User currentUser,
                            @RequestParam("email") String email,
                            Model model) {
        userService.editEmail(currentUser, email);
        model.addAttribute("emailMessage", "Check your email for the changes to take effect");
        model.addAttribute("user", currentUser);
        return "profile";
    }

    @PostMapping("edit/edit-password")
    public String editPassword(@AuthenticationPrincipal User currentUser,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("repNewPassword") String repNewPassword,
                               Model model) {
        if (userService.updatePassword(currentUser, newPassword, repNewPassword)){
            return "redirect:/profile";
        }
        model.addAttribute("repNewPasswordError", "Passwords don't match");
        return "edit-password";
    }
}
