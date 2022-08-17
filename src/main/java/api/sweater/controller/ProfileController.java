package api.sweater.controller;

import api.sweater.model.User;
import api.sweater.service.impl.UserServiceImpl;
import api.sweater.service.interfaces.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import static api.sweater.util.ControllerUtils.getByteArray;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private static final String USER_EXISTS = "User with this username exists, try another username!";
    private static final String CHANGE_EMAIL_MESSAGE = "Check your email for the changes to take effect";
    private static final String CHANGE_PASSWORD_ERROR = "Passwords don't match";
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
                               @RequestParam("file") MultipartFile file) {
        ModelAndView modelAndView = new ModelAndView("redirect:/profile");

        byte[] bArray = getByteArray(file);
        currentUser.setAvatar(bArray);
        userService.save(currentUser);

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
            model.addAttribute("usernameMessage", USER_EXISTS);
            return "edit-username-page";
        }

        return "redirect:/profile";
    }

    @PostMapping("edit/edit-email")
    public ModelAndView editEmail(@AuthenticationPrincipal User currentUser,
                            @RequestParam("email") String email) {
        ModelAndView modelAndView = new ModelAndView("profile-page");

        userService.editEmail(currentUser, email);

        modelAndView.addObject("emailMessage", CHANGE_EMAIL_MESSAGE);
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

        model.addAttribute("repNewPasswordError", CHANGE_PASSWORD_ERROR);
        return "edit-password-page";
    }
}
