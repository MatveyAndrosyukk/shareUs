package api.sweater.controller;

import api.sweater.model.User;
import api.sweater.model.dto.CaptchaResponseDto;
import api.sweater.service.impl.CaptchaServiceImpl;
import api.sweater.service.impl.UserServiceImpl;
import api.sweater.service.interfaces.CaptchaService;
import api.sweater.service.interfaces.UserService;
import api.sweater.util.BindingResultUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class RegistrationController {
    private static final String USER_ACTIVATED = "User successfully activated";
    private static final String EMAIL_CONFIRM_MESSAGE = "Please, activate your account";
    private static final String ACTIVATION_CODE_NOT_FOUND = "Activation code is not found!";
    private static final String USER_EXISTS = "User with this username exists, try another username!";
    private static final String CAPTCHA_NOT_FILLED = "Fill captcha";
    private final UserService userService;
    private final CaptchaService captchaService;

    public RegistrationController(UserServiceImpl userService, CaptchaServiceImpl captchaService) {
        this.userService = userService;
        this.captchaService = captchaService;
    }

    @GetMapping("/registration")
    public ModelAndView registrationPage() {
        ModelAndView modelAndView =new ModelAndView("registration-page");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/registration")
    public String registration(@RequestParam("g-recaptcha-response") String captchaResponse,
                               @ModelAttribute @Valid User user,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = BindingResultUtils.getErrors(bindingResult);

            model.mergeAttributes(errors);
            return "registration-page";
        }

        CaptchaResponseDto response = captchaService.getCaptchaResponse(captchaResponse);
        if (!response.isSuccess()) {
            model.addAttribute("captchaError", CAPTCHA_NOT_FILLED);
        }

        if (!userService.trySave(user)) {
            model.addAttribute("usernameMessage", USER_EXISTS);
            return "registration-page";
        }

        model.addAttribute("confirmEmail", EMAIL_CONFIRM_MESSAGE);
        return "registration-page";
    }

    @GetMapping("/activate/{code}")
    public ModelAndView activate(@PathVariable String code) {
        ModelAndView modelAndView = new ModelAndView("login-page");

        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            modelAndView.addObject("message", USER_ACTIVATED);
        } else {
            modelAndView.addObject("message", ACTIVATION_CODE_NOT_FOUND);
        }

        return modelAndView;
    }
}
