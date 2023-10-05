package xyz.belochka.junit.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import xyz.belochka.junit.dto.User;
import xyz.belochka.junit.paramresolver.UserServiceParamResolver;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({
        UserServiceParamResolver.class
})
//@TestMethodOrder(MethodOrderer.MethodName.class)
class UserServiceTest {
    private static final User VASYA = User.of(1, "Vasya", "123");
    private static final User FEDYA = User.of(2, "Fedya", "1234");
    private static UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    void initAll() {
        System.out.println("Before all " + this);
    }

    @BeforeEach
    void prepare(UserService userService) {
        System.out.println("Before each " + this);
        this.userService = userService;
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

        @ParameterizedTest(name = "{arguments} test")
        @MethodSource("xyz.belochka.junit.service.UserServiceTest#getArgumentsForLoginTest")
//        @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//        @CsvSource({
//                "Vasya,123",
//                "Fedya,1234"
//        })
        void loginPaqrametrizedTest(String username, String password, Optional<User> user) {
            userService.add(VASYA, FEDYA);
            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }
    }

    static Stream<Arguments> getArgumentsForLoginTest() {
        return Stream.of(
                Arguments.of("Vasya", "123", Optional.of(VASYA)),
                Arguments.of("Fedya", "1234", Optional.of(FEDYA)),
                Arguments.of("Fedya", "Incorrect", Optional.empty()),
                Arguments.of("Incorrect", "123", Optional.empty())
        );
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
