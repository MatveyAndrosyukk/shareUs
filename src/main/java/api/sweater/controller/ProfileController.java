package api.sweater.controller;

import api.sweater.model.User;
import api.sweater.service.impl.UserServiceImpl;
import api.sweater.service.interfaces.UserService;
import api.sweater.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView getProfilePage(@AuthenticationPrincipal User currentUser) {
        ModelAndView modelAndView = new ModelAndView("profile-page");

        User user = userService.findById(currentUser.getId());

        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("activate-email/{email}/{code}")
    public ModelAndView activateEmail(@AuthenticationPrincipal User currentUser,
                                @PathVariable("email") String email,
                                @PathVariable("code") String code) {
        ModelAndView modelAndView = new ModelAndView("redirect:/profile");

        userService.activateEmail(currentUser, email, code);

        return modelAndView;
    }


    @PostMapping("changeAvatar")
    public ModelAndView changeAvatar(@AuthenticationPrincipal User currentUser,
                               @RequestParam("file") MultipartFile file) throws IOException {
        ModelAndView modelAndView = new ModelAndView("redirect:/profile");

        User user = userService.findById(currentUser.getId());

        FileUtils.deleteFile(user.getImageFilename(), uploadPath);

        userService.editAvatar(file, currentUser);

        return modelAndView;
    }

    @GetMapping("edit")
    public String getEditPage(@RequestParam() String e) {
        return switch (e) {
            case "username" -> "edit-username-page";
            case "email" -> "edit-email-page";
            case "password" -> "edit-password-page";
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
            return "edit-username-page";
        }

        return "redirect:/profile";
    }

    @PostMapping("edit/edit-email")
    public ModelAndView editEmail(@AuthenticationPrincipal User currentUser,
                            @RequestParam("email") String email) {
        ModelAndView modelAndView = new ModelAndView("profile-page");

        userService.editEmail(currentUser, email);

        modelAndView.addObject("emailMessage", "Check your email for the changes to take effect");
        modelAndView.addObject("user", currentUser);
        return modelAndView;
    }

    @PostMapping("edit/edit-password")
    public String editPassword(@AuthenticationPrincipal User currentUser,
                               @RequestParam("newPassword") String newPassword,
                               @RequestParam("repNewPassword") String repNewPassword,
                               Model model) {
        if (userService.editPassword(currentUser, newPassword, repNewPassword)){
            return "redirect:/profile";
        }

        model.addAttribute("repNewPasswordError", "Passwords don't match");
        return "edit-password-page";
    }
}
