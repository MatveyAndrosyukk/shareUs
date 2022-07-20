package api.sweater.service;

import api.sweater.model.dto.CaptchaResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class CaptchaService {
    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s")
    private String CAPTCHA_URL;

    private final RestTemplate restTemplate;

    public CaptchaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CaptchaResponseDto getCaptchaResponse(String captchaResponse) {
        String url = String.format(CAPTCHA_URL, recaptchaSecret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);

        return response;
    }
}
