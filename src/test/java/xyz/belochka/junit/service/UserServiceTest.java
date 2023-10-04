package xyz.belochka.junit.service;

import org.junit.jupiter.api.*;
import xyz.belochka.junit.dto.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
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
        userService.add(new User());
        userService.add(new User());
        List<User> users = userService.getAll();
        assertEquals(2, users.size());
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
