package api.sweater.controller;

import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.service.impl.UserServiceImpl;
import api.sweater.service.interfaces.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public String userList(@AuthenticationPrincipal User currentUser,
                           @RequestParam(required = false) Long u,
                           Model model) {
        if (u != null) {
            User user = userService.findById(u);
            if (user.getRoles().contains(new Role("ADMIN"))) {
                model.addAttribute("deleteMessage", "You can't delete user with role 'ADMIN'");
                model.addAttribute("userToDelete", user);
            }else {
                userService.deleteById(user.getId());
            }
        }
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
