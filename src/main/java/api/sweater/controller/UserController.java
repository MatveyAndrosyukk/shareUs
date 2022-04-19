package api.sweater.controller;

import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "users";
    }

    @GetMapping("{user}")
    public String editUser(@PathVariable User user,
                           Model model
    ) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.allRoles);
        return "edit-user";
    }

    @PostMapping()
    public String saveUser(@RequestParam Map<String, String> form,
                           @RequestParam("id") Long id,
                           @RequestParam("username") String username,
                           @RequestParam("active") boolean active
    ) {
        User user = userRepository.getById(id);
        user.getRoles().clear();

        for (String key: form.keySet()){
            if (Role.allRoles.contains(new Role(key))){
                user.getRoles().add(new Role(key));
            }
        }

        user.setUsername(username);
        user.setActive(active);

        userRepository.save(user);
        return "redirect:/user";
    }
}
