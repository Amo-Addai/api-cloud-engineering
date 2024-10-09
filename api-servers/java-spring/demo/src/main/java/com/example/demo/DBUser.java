package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DBUser {

    private List<User> userTable = new ArrayList<User>();
    // Arrays.asList(new User(..), new User(..), ..);

    public DBUser() {
        userTable.add(new User(0, "A", 0));
        userTable.add(new User(1, "Auto", 30));
    }

    public List<User> getAllUsers() {
        return userTable;
    }

    public User getUser(int id) {
        for (User user : userTable) {
            if (user.getId() == id)
                return user;
        }
        return null;
    }

    public User getUserF(int id) {
        AtomicReference<User> user = new AtomicReference<User>();
        userTable // .stream() // only required for . map/reduce/extra
                 .forEach(u -> {
                     if (u.getId() == id)
                        user.set(u);
                     // Variable used in lambda expression should be final or effectively final
                     // but if final, cannot reassign to it; so converted to an atomic reference instead
                 });
        return user.get();
    }

    public User getUserByName(String name) {
        Optional<User> user =
                userTable.stream()
                         .filter(u -> u.getName().equals(name))
                         .findFirst();
        // return user.isPresent() ? user.get() : null;
        return user.orElse(null); // better Optional default-value getter
    }

    public User createUser(User user) {
        userTable.add(user);
        return user;
    }

    public boolean addUser(User user) {
        userTable.add(user);
        return true;
    }

    public boolean deleteUser(int id) {
        for (User user : userTable) {
            if (user.getId() == id) {
                userTable.remove(user); // List removes user object by reference-id
                return true;
            }
        }
        return false;
    }

    public boolean deleteUserF(int id) {
        userTable = userTable.stream()
                 .filter(u -> u.getId() != id)
                 .collect(Collectors.toList());
        // filter out all users with different IDs (unnecessary)
        return true;
    }

    // @FunctionalInterface - Function must be invoked by .apply()
    public Function<Integer, Boolean> deleteUserFI = (id) -> {
        userTable.remove(this.getUserF(id));
        return true;
    };

}
