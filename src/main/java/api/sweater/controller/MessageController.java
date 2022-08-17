package api.sweater.controller;

import api.sweater.model.Message;
import api.sweater.model.Pager;
import api.sweater.model.User;
import api.sweater.model.dto.MessageDto;
import api.sweater.service.impl.MessageServiceImpl;
import api.sweater.service.impl.UserServiceImpl;
import api.sweater.service.interfaces.MessageService;
import api.sweater.service.interfaces.UserService;
import api.sweater.util.BindingResultUtils;
import api.sweater.util.ControllerUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static api.sweater.util.ControllerUtils.getByteArray;

@Controller
public class MessageController {
    private static final int BUTTONS_TO_SHOW = 5;
    private static final int INITIAL_PAGE = 0;
    private static final int INITIAL_PAGE_SIZE = 5;
    private final MessageService messageService;
    private final UserService userService;

    public MessageController(MessageServiceImpl messageService, UserServiceImpl userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView greeting(@AuthenticationPrincipal User currentUser) {
        ModelAndView modelAndView = new ModelAndView("greeting-page");
        modelAndView.addObject("username", currentUser.getUsername());
        modelAndView.addObject("user", currentUser);
        return modelAndView;
    }

    @GetMapping("/messages")
    public ModelAndView getMessagesPage(@RequestParam(required = false) String filter,
                                        @RequestParam("page") Optional<Integer> page,
                                        @AuthenticationPrincipal User currentUser) {
        ModelAndView modelAndView = new ModelAndView("messages-page");

        int evalPage = page.filter(p -> p >= 1)
                .map(p -> p - 1)
                .orElse(INITIAL_PAGE);

        PageRequest pageRequest = PageRequest.of(evalPage, INITIAL_PAGE_SIZE);

        Page<MessageDto> messages = messageService.getMessages(filter, pageRequest, modelAndView, currentUser);

        var pager = new Pager(messages.getTotalPages(), messages.getNumber(), BUTTONS_TO_SHOW);

        modelAndView.addObject("messages", messages);
        modelAndView.addObject("pageSize", INITIAL_PAGE_SIZE);
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("user", currentUser);
        return modelAndView;
    }

    @PostMapping("/messages")
    public ModelAndView addMessage(@AuthenticationPrincipal User currentUser,
                                   @Valid Message message,
                                   BindingResult bindingResult,
                                   @RequestParam("file") MultipartFile file,
                                   @RequestParam("page") Integer page) {
        ModelAndView modelAndView = new ModelAndView("messages-page");

        byte[] bArray = getByteArray(file);
        message.setImage(bArray);
        message.setAuthor(currentUser);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = BindingResultUtils.getErrors(bindingResult);
            modelAndView.addAllObjects(errorsMap);
            modelAndView.addObject("message", message);
        } else {
            messageService.save(message);
            modelAndView.addObject("message", null);
        }

        Page<MessageDto> messages = messageService.findAll(PageRequest.of(page, INITIAL_PAGE_SIZE), currentUser);
        var pager = new Pager(messages.getTotalPages(), messages.getNumber(), BUTTONS_TO_SHOW);

        modelAndView.addObject("messages", messages);
        modelAndView.addObject("user", currentUser);
        modelAndView.addObject("pager", pager);
        return modelAndView;
    }

    @GetMapping("/messages/{author}")
    public ModelAndView getAuthorMessages(@AuthenticationPrincipal User currentUser,
                                          @PathVariable("author") String authorName,
                                          @RequestParam("size") Optional<Integer> size,
                                          @RequestParam("page") Optional<Integer> page) {
        ModelAndView modelAndView = new ModelAndView("author's-messages-page");

        User author = userService.findByUsername(authorName);

        int evalPage = page.filter(p -> p >= 1)
                .map(p -> p - 1)
                .orElse(INITIAL_PAGE);

        Page<MessageDto> messages = messageService.findByAuthor(author, currentUser, PageRequest.of(evalPage, INITIAL_PAGE_SIZE));

        var pager = new Pager(messages.getTotalPages(), messages.getNumber(), BUTTONS_TO_SHOW);

        modelAndView.addObject("messages", messages);
        modelAndView.addObject("currentUser", currentUser);
        modelAndView.addObject("author", author);
        modelAndView.addObject("pager", pager);
        modelAndView.addObject("pageSize", INITIAL_PAGE_SIZE);
        return modelAndView;
    }

    @PostMapping("/messages/delete-message/{id}")
    public ModelAndView deleteMessage(@PathVariable("id") Long id,
                                      @RequestHeader(required = false) String referer,
                                      RedirectAttributes redirectAttributes) {
        UriComponents components = ControllerUtils.getUriComponents(referer, redirectAttributes);

        ModelAndView modelAndView = new ModelAndView("redirect:" + components.getPath());

        messageService.deleteById(id);

        return modelAndView;
    }

    @GetMapping("/messages/{message}/like")
    public ModelAndView like(@AuthenticationPrincipal User currentUser,
                             @PathVariable Message message,
                             @RequestHeader(required = false) String referer,
                             RedirectAttributes redirectAttributes
    ) {
        UriComponents components = ControllerUtils.getUriComponents(referer, redirectAttributes);

        ModelAndView modelAndView = new ModelAndView("redirect:" + components.getPath());

        Set<User> likes = message.getLikes();
        if (likes.contains(currentUser)) {
            likes.remove(currentUser);
        } else {
            likes.add(currentUser);
        }

        return modelAndView;
    }
}
