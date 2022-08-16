package api.sweater.util;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

public class ControllerUtils {
    public static UriComponents getUriComponents(String referer, RedirectAttributes redirectAttributes){
        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams()
                .entrySet()
                .forEach(pair -> {
                    redirectAttributes.addAttribute(pair.getKey(), pair.getValue());
                });
        return components;
    }
}
