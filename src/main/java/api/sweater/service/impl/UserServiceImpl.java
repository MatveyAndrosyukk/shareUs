package api.sweater.service.impl;

import api.sweater.exception.ResourceNotFoundException;
import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.repository.interfaces.UserRepository;
import api.sweater.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserDetailsService, UserService {
    @Value("${upload.path}")
    private String uploadPath;
    @Value("${hostname}")
    private String hostname;
    private final UserRepository userRepository;
    private final MailServiceImpl mailServiceImpl;

    public UserServiceImpl(UserRepository userRepository, MailServiceImpl mailServiceImpl) {
        this.userRepository = userRepository;
        this.mailServiceImpl = mailServiceImpl;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findUser = userRepository.findByUsername(username);

        if (findUser == null) {
            throw new UsernameNotFoundException("There is no user with this username");
        }

        return findUser;
    }


    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exists!"));
    }

    public User findByUsername(String authorName) {
        return userRepository.findByUsername(authorName);
    }

    public void save(User user){
        userRepository.save(user);
    }

    public boolean trySave(User user) {
        User existedUser = userRepository.findByUsername(user.getUsername());
        if (existedUser != null) {
            return false;
        }

        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setActive(false);
        user.setRoles(List.of(new Role("USER")));

        userRepository.save(user);

        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format("Hello, %s, \n" +
                            "Welcome to Sweater. Please, visit next link: https://%s/activate/%s",
                    user.getUsername(),
                    hostname,
                    user.getActivationCode());
            mailServiceImpl.send(user.getEmail(), "Activation Code", message);
        }

        return true;
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);

        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);

        return true;
    }

    public void activateEmail(User user, String email, String code) {
        User userByActivationCode = userRepository.findByActivationCode(code);
        if (userByActivationCode == null) {
            return;
        }
        user.setEmail(email);
        user.setActivationCode(null);
        userRepository.save(user);
    }

    public void editUser(Map<String, String> form, Long id, String username, boolean active) {
        User user = userRepository.getById(id);
        user.getRoles().clear();

        for (String key : form.keySet()) {
            if (Role.allRoles.contains(new Role(key))) {
                user.getRoles().add(new Role(key));
            }
        }
        user.setUsername(username);
        user.setActive(active);

        userRepository.save(user);
    }

    public boolean editUsername(User user, String username) {
        if (userRepository.findByUsername(username) != null) {
            return false;
        }
        user.setUsername(username);
        userRepository.save(user);
        return true;
    }

    public void editEmail(User user, String email) {
        user.setActivationCode(UUID.randomUUID().toString());

        userRepository.save(user);

        String message = String.format("Hello, %s, \n" +
                        "Please, visit next link to change your email: http://localhost:8080/profile/activate-email/%s/%s",
                user.getUsername(),
                email,
                user.getActivationCode());
        mailServiceImpl.send(email, "Activation Code", message);
    }

    public boolean editPassword(User user, String newPassword, String repNewPassword) {
        if (newPassword.equals(repNewPassword)) {
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;

    }

    public void editAvatar(MultipartFile file, User user) throws IOException {
        if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()){
            File uploadDir = new File(uploadPath + "/profile-images");
            if (!uploadDir.exists()){
                uploadDir.mkdir();
            }
            String fileName = UUID.randomUUID() + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/profile-images/" + fileName));
            user.setImageFilename(fileName);
            save(user);
        }
    }
}
