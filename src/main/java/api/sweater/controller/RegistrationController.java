package api.sweater.controller;

import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.repository.UserRepository;
import api.sweater.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class RegistrationController {
    private final UserRepository userRepository;

    private final UserService userService;

    public RegistrationController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute User user, Model model){
        User checkUser = userRepository.findByUsername(user.getUsername());
        if (checkUser != null){
            model.addAttribute("message", "User exists!");
            return "registration";
        }
        user.setActive(true);
        user.setRoles(Arrays.asList(new Role("USER")));
        userService.save(user);
        return "redirect:/login";
    }
}
