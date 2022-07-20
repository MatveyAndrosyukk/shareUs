package api.sweater.controller;

import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.repository.UserRepository;
import api.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userList(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("user", currentUser);
        return "users";
    }

    @GetMapping("{user}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String editUser(@PathVariable User user,
                           Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.allRoles);
        return "edit-user";
    }

    @GetMapping("/delete/{user}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String removeUser(@AuthenticationPrincipal User currentUser,
                             @PathVariable User user,
                             Model model) {
        if (user.getRoles().contains(new Role("ADMIN"))) {
            model.addAttribute("unableToDelete", "You can't delete user with role 'ADMIN'");
            model.addAttribute("checkedUser", user);
            model.addAttribute("users", userService.findAll());
            model.addAttribute("user", currentUser);
            return "users";
        }
        userService.deleteById(user.getId());

        return "redirect:/user";
    }

    @GetMapping("/subscribe/{author}")
    public String subscribe(@PathVariable("author") User author,
                            @AuthenticationPrincipal User currentUser) {
        author.getSubscribers().add(currentUser);
        currentUser.getSubscriptions().add(author);
        userService.save(author);
        return "redirect:/main/" + author.getUsername();
    }

    @GetMapping("/unsubscribe/{author}")
    public String unsubscribe(@PathVariable("author") User author,
                              @AuthenticationPrincipal User currentUser) {
        author.getSubscribers().remove(currentUser);
        currentUser.getSubscriptions().remove(author);
        userService.save(author);
        return "redirect:/main/" + author.getUsername();
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public String saveUser(@RequestParam Map<String, String> form,
                           @RequestParam("id") Long id,
                           @RequestParam("username") String username,
                           @RequestParam("active") boolean active) {
        userService.editUser(form, id, username, active);
        return "redirect:/user";
    }
}
