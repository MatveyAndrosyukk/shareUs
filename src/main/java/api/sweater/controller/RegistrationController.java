package api.sweater.controller;

import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.repository.UserRepository;
import api.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String registrationPage(){
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(User user, Map<String, Object> model){
        User checkUser = userRepository.findByUsername(user.getUsername());
        if (checkUser != null){
            model.put("message", "User exists!");
            return "registration";
        }
        user.setActive(true);
        user.setRoles(Arrays.asList(new Role("USER")));
        userService.save(user);
        return "redirect:/login";
    }
}
