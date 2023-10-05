package xyz.belochka.junit.service;

import org.junit.jupiter.api.*;
import xyz.belochka.junit.dto.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@TestMethodOrder(MethodOrderer.MethodName.class)
class UserServiceTest {
    private static final User VASYA = User.of(1, "Vasya", "123");
    private static final User FEDYA = User.of(2, "Fedya", "1234");
    private static UserService userService;

    @BeforeAll
    void initAll() {
        System.out.println("Before all " + this);
    }
    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
        userService = new UserService();
    }
    @Tag("user")
    @Nested
    class UserTests {
        @Test
        void usersEmptyIfNotAdded() {
            System.out.println("Test 1 " + this);
            List<User> users = userService.getAll();
            assertTrue(users.isEmpty());
        }
        @Test
        void usersSizeIfUserAdded() {
            System.out.println("Test 2 " + this);
            userService.add(VASYA, FEDYA);
            List<User> users = userService.getAll();
            assertThat(users).hasSize(2);
//        assertEquals(2, users.size());
        }
        @Test
        void usersConvertedToMapById() {
            userService.add(VASYA, FEDYA);
            Map<Integer, User> users = userService.getAllConverted();
            assertAll(
                    () -> assertThat(users).containsKeys(VASYA.getId(), FEDYA.getId()),
                    () -> assertThat(users).containsValues(VASYA, FEDYA)
            );
        }
    }
    @Tag("login")
    @Nested
    class LoginTests {
        @Test
        void loginSuccesIfUserExist() {
            userService.add(VASYA);
            Optional<User> maybeUser = userService.login(VASYA.getUsername(), VASYA.getPassword());
            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(VASYA));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(VASYA,user));
        }
        @Test
        void loginFailIfPasswordNotCorrect() {
            userService.add(VASYA);
            Optional<User> maybeUser = userService.login(VASYA.getUsername(), "Incorrect");
            assertTrue(maybeUser.isEmpty());
        }
        @Test
        void loginFailIfUserDoesNotExist() {
            userService.add(VASYA);
            Optional<User> maybeUser = userService.login("Incorrect", VASYA.getPassword());
            assertTrue(maybeUser.isEmpty());
        }
        @Test
        void throwExeptionIfLoginOrPasswordNull() {
            assertAll(
                    () -> {
                        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.login(null, "Incorrect"));
                        assertThat(exception.getMessage()).isEqualTo("Username or password is null");
                    },
                    () -> {
                        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> userService.login("Incorrect", null));
                        assertThat(exception1.getMessage()).isEqualTo("Username or password is null");
                    }
            );
//        assertThrows(IllegalArgumentException.class, () -> userService.login(null, "Incorrect"));
        }
    }

    @AfterEach
    void deleteData() {
        System.out.println("After each " + this);
    }
    @AfterAll
    void deleteAll() {
        System.out.println("After all " + this);
    }
}
