package api.sweater.controller;

import api.sweater.model.Message;
import api.sweater.model.User;
import api.sweater.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class MainController {
    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/")
    public String greeting(){
        return "greeting";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model){
        model.put("messages", messageRepository.findAll());
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @RequestParam String text,
            @RequestParam String tag,
            Map<String, Object> model
    ){
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
