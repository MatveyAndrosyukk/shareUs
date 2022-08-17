package api.sweater.controller;

import api.sweater.model.Message;
import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.model.dto.MessageDto;
import api.sweater.service.impl.UserServiceImpl;
import api.sweater.service.interfaces.MessageService;
import api.sweater.service.interfaces.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final String DELETE_ADMIN_MESSAGE = "You can't delete user with role 'ADMIN'";
    private final UserService userService;
    private final MessageService messageService;

    public UserController(UserServiceImpl userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView userList(@AuthenticationPrincipal User currentUser,
                           @RequestParam(required = false) Long u) {
        ModelAndView modelAndView = new ModelAndView("users-page");

        if (u != null) {
            User user = userService.findById(u);
            if (user.getRoles().contains(new Role("ADMIN"))) {
                modelAndView.addObject("deleteMessage", DELETE_ADMIN_MESSAGE);
                modelAndView.addObject("userToDelete", user);
            }else {
                List<Message> messages = messageService.findAll();
                messages.forEach(message -> {
                    message.getLikes().remove(user);
                    messageService.save(message);
                });
                userService.deleteById(user.getId());
            }
        }

        modelAndView.addObject("users", userService.findAll());
        modelAndView.addObject("user", currentUser);
        return modelAndView;
    }

    @GetMapping("{user}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView editUser(@PathVariable User user) {
        ModelAndView modelAndView = new ModelAndView("edit-user-page");
        modelAndView.addObject("user", user);
        modelAndView.addObject("roles", Role.allRoles);
        return modelAndView;
    }

    @GetMapping("/subscribe/{author}")
    public ModelAndView subscribe(@PathVariable("author") User author,
                            @AuthenticationPrincipal User currentUser) {
        ModelAndView modelAndView = new ModelAndView("redirect:/messages/" + author.getUsername());

        author.getSubscribers().add(currentUser);
        currentUser.getSubscriptions().add(author);

        userService.save(author);

        return modelAndView;
    }

    @GetMapping("/unsubscribe/{author}")
    public ModelAndView unsubscribe(@PathVariable("author") User author,
                              @AuthenticationPrincipal User currentUser) {
        ModelAndView modelAndView = new ModelAndView("redirect:/messages/" + author.getUsername());

        author.getSubscribers().remove(currentUser);
        currentUser.getSubscriptions().remove(author);

        userService.save(author);

        return modelAndView;
    }

    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ModelAndView saveUser(@RequestParam Map<String, String> form,
                           @RequestParam("id") Long id,
                           @RequestParam("username") String username,
                           @RequestParam("active") boolean active) {
        ModelAndView modelAndView = new ModelAndView("redirect:/user");

        userService.editUser(form, id, username, active);

        return modelAndView;
    }
}
