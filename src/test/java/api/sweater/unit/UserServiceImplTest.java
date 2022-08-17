package api.sweater.unit;

import api.sweater.model.Role;
import api.sweater.model.User;
import api.sweater.repository.interfaces.UserRepository;
import api.sweater.service.impl.MailServiceImpl;
import api.sweater.service.impl.UserServiceImpl;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MailServiceImpl mailService;

    @Test
    void trySave() {
        User user = new User();
        user.setUsername("username");
        user.setEmail("email@mail.ru");
        user.setPassword("secret");

        boolean isUserCreated = userService.trySave(user);

        assertTrue(isUserCreated);
        assertFalse(user.isActive());
        assertNotNull(user.getActivationCode());
        assertTrue(CoreMatchers.is(user.getRoles()).matches(List.of(new Role("USER"))));

        Mockito.verify(userRepository, Mockito.times(1))
                .findByUsername(
                        ArgumentMatchers.eq(user.getUsername())
                );
        Mockito.verify(mailService, Mockito.times(1))
                .send(
                        ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.eq("Activation Code"),
                        ArgumentMatchers.eq("Hello, username, \n" +
                                "Welcome to Sweater. Please, visit next link: https://localhost:8080/activate/"
                                + user.getActivationCode()));
    }

    @Test
    void trySaveFail() {
        User user = new User();
        user.setUsername("admin");
        user.setPassword("secret");

        Mockito.doReturn(new User())
                .when(userRepository)
                .findByUsername(user.getUsername());

        boolean isUserCreated = userService.trySave(user);

        assertFalse(isUserCreated);

        Mockito.verify(userRepository, Mockito.times(1))
                .findByUsername(
                        ArgumentMatchers.eq(user.getUsername())
                );
        Mockito.verify(mailService, Mockito.times(0))
                .send(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());
    }

    @Test
    public void activateUser() {
        User user = new User();
        user.setActivationCode("Activation Code");

        Mockito.doReturn(user)
                .when(userRepository)
                .findByActivationCode(user.getActivationCode());

        boolean isUserActivated = userService.activateUser("Activation Code");

        assertTrue(isUserActivated);
        assertNull(user.getActivationCode());

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void activateUserFail() {
        Mockito.doReturn(null)
                .when(userRepository)
                .findByActivationCode(ArgumentMatchers.anyString());

        boolean isUserActivated = userService.activateUser("Activation Code");

        assertFalse(isUserActivated);

        Mockito.verify(userRepository, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void findByIdThrowsException() {
        Mockito.doReturn(null)
                .when(userRepository)
                .findById(ArgumentMatchers.anyLong());

        assertThrows(NullPointerException.class, () -> {
            userService.findById(1L);
        });
    }
}