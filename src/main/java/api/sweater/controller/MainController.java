package api.sweater.controller;

import api.sweater.model.Message;
import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.repository.MessageRepository;
import api.sweater.repository.UserRepository;
import api.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    public MainController(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String greeting(@AuthenticationPrincipal UserDetails userDetails,
                           Map<String, Object> model){
        User user = userRepository.findByUsername(userDetails.getUsername());
        model.put("username", user.getUsername());
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model){
        model.put("messages", messageRepository.findAll());
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String text,
            @RequestParam String tag
    ){
        User user = userRepository.findByUsername(userDetails.getUsername());
        messageRepository.save(new Message(text, tag, user));
        return "redirect:/main";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam String filter,
                         Map<String, Object> model
    ){
        if (filter != null && !filter.isEmpty()){
            model.put("messages", messageRepository.findByTag(filter));
        }else {
            model.put("messages", messageRepository.findAll());
        }
        return "main";
    }
}
