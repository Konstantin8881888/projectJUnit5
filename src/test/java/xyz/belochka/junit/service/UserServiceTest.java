package xyz.belochka.junit.service;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mockito;
import xyz.belochka.junit.dao.UserDao;
import xyz.belochka.junit.dto.User;
import xyz.belochka.junit.extension.ConditionalExtension;
import xyz.belochka.junit.extension.GlobalExtension;
import xyz.belochka.junit.extension.UserServiceParamResolver;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({
        UserServiceParamResolver.class,
        GlobalExtension.class,
        ConditionalExtension.class,
//        ThrowableExtension.class
})
//@TestMethodOrder(MethodOrderer.MethodName.class)
class UserServiceTest {
    private static final User VASYA = User.of(1, "Vasya", "123");
    private static final User FEDYA = User.of(2, "Fedya", "1234");
    private UserDao userDao;
    private UserService userService;

    UserServiceTest(TestInfo testInfo) {
        System.out.println();
    }

    @BeforeAll
    void initAll() {
        System.out.println("Before all " + this);
    }

    @BeforeEach
    void prepare() {
        System.out.println("Before each " + this);
//        this.userDao = Mockito.mock(UserDao.class);
        this.userDao = Mockito.spy(new UserDao());
        this.userService = new UserService(userDao);
    }

    @Test
    void shouldDeleteExistedUser(){
        userService.add(VASYA);
        Mockito.doReturn(true).when(userDao).delete(VASYA.getId());
//        Mockito.doReturn(true).when(userDao).delete(Mockito.any());
// Mockito.when(userDao.delete(VASYA.getId())).thenReturn(true);// Работает не для всех случаев
        boolean deleteResult = userService.delete(VASYA.getId());
        Mockito.verify(userDao, Mockito.times(1)).delete(VASYA.getId());
        assertThat(deleteResult).isTrue();
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
            System.out.println("Test 3 " + this);
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
            System.out.println("Test 4 " + this);
            userService.add(VASYA);
            Optional<User> maybeUser = userService.login(VASYA.getUsername(), VASYA.getPassword());
            assertThat(maybeUser).isPresent();
            maybeUser.ifPresent(user -> assertThat(user).isEqualTo(VASYA));
//        assertTrue(maybeUser.isPresent());
//        maybeUser.ifPresent(user -> assertEquals(VASYA,user));
        }

        @Test
        void loginFailIfPasswordNotCorrect() {
            System.out.println("Test 5 " + this);
            userService.add(VASYA);
            Optional<User> maybeUser = userService.login(VASYA.getUsername(), "Incorrect");
            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void loginFailIfUserDoesNotExist() {
            System.out.println("Test 6 " + this);
            userService.add(VASYA);
            Optional<User> maybeUser = userService.login("Incorrect", VASYA.getPassword());
            assertTrue(maybeUser.isEmpty());
        }

        @Test
        void throwExeptionIfLoginOrPasswordNull() throws IOException {
//            if (true){
//                throw new IOException();
//            }
            System.out.println("Test 7 " + this);
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
            System.out.println("Test 8 " + this);
            userService.add(VASYA, FEDYA);
            Optional<User> maybeUser = userService.login(username, password);
            assertThat(maybeUser).isEqualTo(user);
        }

        @Test
        void checkLoginFunctionalityPerfofmance() {
            System.out.println("Test 9 " + this);
            Optional<User> result = assertTimeout(Duration.ofMillis(100L), () -> {
//                Thread.sleep(300);
                return userService.login(VASYA.getUsername(), "Incorrect");
            });
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
