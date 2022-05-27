package api.sweater.controller;

import api.sweater.model.Message;
import api.sweater.model.User;
import api.sweater.repository.MessageRepository;
import api.sweater.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Controller
public class MainController {
    private final MessageRepository messageRepository;

    private final UserRepository userRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public MainController(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String greeting(@AuthenticationPrincipal User user,
                           Model model
    ) {
        model.addAttribute("username", user.getUsername());
        model.addAttribute("user", user);
        return "greeting";
    }

    @GetMapping("/main")
    public String main(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String filter,
            Model model
    ) {
        List<Message> messages = new ArrayList<>();
        if (filter != null && !filter.isEmpty()) {
            Iterable<Message> allMessages = messageRepository.findAll();
            String[] tags = filter.split(" ");
            for (Message message : allMessages) {
                String[] messageTags = message.getTag().split(" ");
                for (String tag:
                     tags) {
                    for (String messageTag: messageTags){
                        if (messageTag.equals(tag)){
                            messages.add(message);
                        }
                    }
                }
            }
            model.addAttribute("filter", filter);
        } else {
            messages = (List<Message>) messageRepository.findAll();
        }
        model.addAttribute("messages", messages);
        model.addAttribute("user", user);
        return "messages";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal User user,
            @Valid Message message,
            BindingResult bindingResult,
            Model model,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        message.setAuthor(user);

        if (bindingResult.hasErrors()){
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute("message", message);
        }else {
            if (file != null && !file.getOriginalFilename().isEmpty()) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                String resultFileName = UUID.randomUUID() + "." + file.getOriginalFilename();
                file.transferTo(new File(uploadPath + "/" + resultFileName));
                message.setFilename(resultFileName);
            }
            messageRepository.save(message);

            model.addAttribute("message", null);
        }

        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);
        model.addAttribute("user", user);
        return "messages";
    }

    @GetMapping("/main/{author}")
    public String getAuthorMessages(@PathVariable("author") String authorName,
                                    Model model){
        User author = userRepository.findByUsername(authorName);
        List<Message> messages = messageRepository.findByAuthor(author);
        model.addAttribute("messages", messages);
        model.addAttribute("author", author);
        System.out.println(messages);
        return "author's-messages";
    }

    @GetMapping("/main/delete-message/{id}")
    public String deleteMessage(@PathVariable("id") Long id){
        messageRepository.deleteById(id);
        return "redirect:/main";
    }
}
