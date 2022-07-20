package api.sweater.controller;

import api.sweater.controller.utils.ControllerUtils;
import api.sweater.exception.ResourceNotFoundException;
import api.sweater.model.User;
import api.sweater.service.UserService;
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

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getProfile(@AuthenticationPrincipal User currentUser, Model model) {
        User user = userService.findById(currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("User does not exists"));
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
        User user = userService.findById(currentUser.getId()).orElseThrow(() -> new ResourceNotFoundException("fd"));
        ControllerUtils.deleteFile(uploadPath, user.getImageFilename());
        userService.changeAvatar(file, uploadPath, currentUser);
        return "redirect:/profile";
    }

    @GetMapping("edit/username")
    public String getEditUsernamePage() {
        return "edit-username";
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

    @GetMapping("edit/email")
    public String getEditEmailPage() {
        return "edit-email";
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

    @GetMapping("edit/password")
    public String getEditPasswordPage() {
        return "edit-password";
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
