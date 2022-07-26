package api.sweater.service.interfaces;

import api.sweater.model.dto.CaptchaResponseDto;

public interface CaptchaService {
    CaptchaResponseDto getCaptchaResponse(String captchaResponse);
}
