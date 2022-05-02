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

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
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
        if (existedUser != null){
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

        if (!StringUtils.isEmpty(saveUser.getEmail())){
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

        if (user == null){
            return false;
        }

        user.setActivationCode(null);
        user.setActive(true);
        userRepository.save(user);

        return true;
    }
}
