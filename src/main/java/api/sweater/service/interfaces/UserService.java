package api.sweater.service.interfaces;

import api.sweater.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> findAll();

    User findById(Long id);

    User findByUsername(String authorName);

    void save(User user);

    boolean trySave(User user);

    void deleteById(Long id);

    boolean activateUser(String code);

    void activateEmail(User user, String email, String code);

    void editUser(Map<String, String> form, Long id, String username, boolean active);

    boolean editUsername(User user, String username);

    void editEmail(User user, String email);

    boolean editPassword(User user, String newPassword, String repNewPassword);

    void editAvatar(MultipartFile file, User user) throws IOException;
}
