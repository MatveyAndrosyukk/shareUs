package api.sweater.controller;

import api.sweater.model.User;
import api.sweater.model.dto.CaptchaResponseDto;
import api.sweater.service.impl.CaptchaServiceImpl;
import api.sweater.service.impl.UserServiceImpl;
import api.sweater.service.interfaces.CaptchaService;
import api.sweater.service.interfaces.UserService;
import api.sweater.service.util.BindingResultUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {
    private final UserService userService;

    private final CaptchaService captchaService;

    public RegistrationController(UserServiceImpl userService, CaptchaServiceImpl captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
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
            Map<String, String> errors = BindingResultUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }
        CaptchaResponseDto response = captchaService.getCaptchaResponse(captchaResponse);
        if (!response.isSuccess()){
            model.addAttribute("captchaError", "Fill captcha");
        }

        if (!userService.trySave(user)){
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
