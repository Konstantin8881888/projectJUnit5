package xyz.belochka.junit.service;

import org.junit.jupiter.api.*;
import xyz.belochka.junit.dto.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private static final User VASYA = User.of(1, "Vasya", "123");
    private static final User FEDYA = User.of(1, "Fedya", "1234");
    private static UserService userService;

    @BeforeAll
    void initAll(){
        System.out.println("Before all " + this);
        userService = new UserService();
    }
    @BeforeEach
    void prepare(){
        System.out.println("Before each " + this);
    }
    @Test
    void usersEmptyIfNotAdded(){
        System.out.println("Test 1 " + this);
        List<User> users = userService.getAll();
        assertTrue(users.isEmpty());
    }

    @Test
    void usersSizeIfUserAdded(){
        System.out.println("Test 2 " + this);
        userService.add(VASYA);
        userService.add(FEDYA);
        List<User> users = userService.getAll();
        assertEquals(2, users.size());
    }

    @Test
    void loginSuccesIfUserExist(){
        userService.add(VASYA);
        Optional<User> maybeUser = userService.login(VASYA.getUsername(), VASYA.getPassword());
        assertTrue(maybeUser.isPresent());
        maybeUser.ifPresent(user -> assertEquals(VASYA,user));
    }

    @Test
    void loginFailIfPasswordNotCorrect(){
        userService.add(VASYA);
        Optional<User> maybeUser = userService.login(VASYA.getUsername(), "Incorrect");
        assertTrue(maybeUser.isEmpty());
    }

    @Test
    void loginFailIfUserDoesNotExist(){
        userService.add(VASYA);
        Optional<User> maybeUser = userService.login("Incorrect", VASYA.getPassword());
        assertTrue(maybeUser.isEmpty());
    }
    @AfterEach
    void deleteData(){
        System.out.println("After each " + this);
    }
    @AfterAll
    void deleteAll(){
        System.out.println("After all " +this);
    }
}
