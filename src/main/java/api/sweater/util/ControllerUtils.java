package api.sweater.util;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

public class ControllerUtils {
    public static UriComponents getUriComponents(String referer, RedirectAttributes redirectAttributes){
        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams()
                .forEach(redirectAttributes::addAttribute);
        return components;
    }

    public static byte[] getByteArray(MultipartFile file){
        byte[] bArray = null;
        if (!file.isEmpty()){
            try{
                bArray = new byte[file.getBytes().length];
                int i = 0;
                for (byte b: file.getBytes()){
                    bArray[i++] = b;
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return bArray;
    }
}
