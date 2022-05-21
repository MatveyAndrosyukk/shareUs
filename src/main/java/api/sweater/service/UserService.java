package api.sweater.service;

import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    private final SendMailService mailService;

    public UserService(UserRepository userRepository, SendMailService mailService) {
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findUser = userRepository.findByUsername(username);

        if (findUser == null) {
            throw new UsernameNotFoundException("There is no user with this username");
        }

        return findUser;
    }

    public Collection<GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
    }

    public boolean save(User user) {
        User existedUser = userRepository.findByUsername(user.getUsername());
        if (existedUser != null) {
            return false;
        }

        User saveUser = new User(
                user.getUsername(),
                new BCryptPasswordEncoder().encode(user.getPassword()),
                user.getEmail(),
                UUID.randomUUID().toString(),
                false,
                Arrays.asList(new Role("USER")));

        userRepository.save(saveUser);

        if (!StringUtils.isEmpty(saveUser.getEmail())) {
            String message = String.format("Hello, %s, \n" +
                            "Welcome to Sweater. Please, visit next link: http://localhost:8080/activate/%s",
                    saveUser.getUsername(),
                    saveUser.getActivationCode());
            mailService.send(saveUser.getEmail(), "Activation Code", message);
        }

        return true;
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

    public List<User> findAll() {
        return userRepository.findAll();
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
        mailService.send(email, "Activation Code", message);
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

    public boolean updatePassword(User user, String newPassword, String repNewPassword) {
        if (newPassword.equals(repNewPassword)) {
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;

    }
}
