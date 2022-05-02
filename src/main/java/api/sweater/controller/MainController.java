package api.sweater.controller;

import api.sweater.model.Message;
import api.sweater.model.User;
import api.sweater.repository.MessageRepository;
import api.sweater.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
@Validated
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
    public String greeting(@AuthenticationPrincipal UserDetails userDetails,
                           Model model
    ) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        model.addAttribute("username", user.getUsername());
        return "greeting";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false)
                       @Pattern(regexp = "#.+", message = "введите #текст не длиннее 30 символов") String filter,
                       @RequestParam(required = false) String filterError,
                       Model model
    ) {
        if (filterError != null) {
            model.addAttribute("filterError", "введите #текст не длиннее 30 символов");
        }
        Iterable<Message> messages;
        if (filter != null && !filter.isEmpty()) {
            messages = messageRepository.findByTag(filter);
        } else {
            messages = messageRepository.findAll();
        }
        model.addAttribute("messages", messages);
        return "messages";
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public String handleConstraintViolationException(ConstraintViolationException e, Model model) {
        model.addAttribute("filterError", "введите #текст не длиннее 30 символов");
        return "redirect:/main";

    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String text,
            @RequestParam @Pattern(regexp = "#.+", message = "введите #текст не длиннее 30 символов") String tag,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        User user = userRepository.findByUsername(userDetails.getUsername());
        Message message = new Message(text, tag, user);

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
        return "redirect:/main";
    }

}
