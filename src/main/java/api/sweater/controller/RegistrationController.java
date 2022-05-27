package api.sweater.controller;

import api.sweater.model.User;
import api.sweater.model.dto.CapchaResponseDto;
import api.sweater.repository.UserRepository;
import api.sweater.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;

@Controller
public class RegistrationController {
    private final UserService userService;

    private final String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Autowired
    private RestTemplate restTemplate;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registrationPage(Model model){
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@RequestParam("g-recaptcha-response") String captchaResponse,
                               @ModelAttribute @Valid User user,
                               BindingResult bindingResult,
                               Model model){
        if (bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }

        String url = String.format(CAPTCHA_URL, recaptchaSecret, captchaResponse);
        CapchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CapchaResponseDto.class);
        if (!response.isSuccess()){
            model.addAttribute("captchaError", "Fill captcha");
        }

        if (!userService.save(user) ){
            model.addAttribute("usernameMessage", "User with this username exists, try another username!");
            return "registration";
        }
        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code, Model model){
        boolean isActivated = userService.activateUser(code);

        if (isActivated){
            model.addAttribute("message", "User successfully activated");
        }else {
            model.addAttribute("message", "Activation code is not found!");
        }
        return "login";
    }
}
