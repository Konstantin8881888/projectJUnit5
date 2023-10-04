package xyz.belochka.junit.service;

import xyz.belochka.junit.dto.User;

import java.util.*;

import static java.util.function.Function.*;
import static java.util.stream.Collectors.toMap;

public class UserService {
    private final List<User> users = new ArrayList<>();

    public List<User> getAll(){
        return users;
    }

    public void add(User... users) {
        this.users.addAll(Arrays.asList(users));
    }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .filter(user -> user.getPassword().equals(password))
                .findFirst();
    }

    public Map<Integer, User> getAllConverted() {
        return users.stream()
                .collect(toMap(User::getId, identity()));
    }
}
