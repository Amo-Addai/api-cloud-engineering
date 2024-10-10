package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DBUser {

    private int currentId = 0;
    private List<User> userTable = new ArrayList<User>();
    // Arrays.asList(new User(..), new User(..), ..);

    public DBUser() {
        currentId++;
        userTable.add(new User(currentId, "A", 0));
        currentId++;
        userTable.add(new User(currentId, "Auto", 30));
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
        currentId++;
        user.setId(currentId);
        userTable.add(user);
        return user;
    }

    public boolean addUser(User user) {
        currentId++;
        user.setId(currentId);
        userTable.add(user);
        return true;
    }

    public User replaceUser(int id, User user) {
        AtomicBoolean set = new AtomicBoolean(false);
        userTable.forEach(u -> {
            if (u.getId() == id) {
                System.out.println(u.toString());
                user.setId(id);
                userTable.remove(u);
                userTable.add(user);
                System.out.println(user.toString());
                set.set(true);
            }
        });
        return set.get() ? user : null;
    }

    public User updateUser(int id, User user) {
        AtomicBoolean set = new AtomicBoolean(false);
        userTable // .stream() // only required for . map/reduce/extra
                .forEach(u -> {
                    if (u.getId() == id) {
                        System.out.println(u.toString());
                        user.setId(u.getId()); // * not required; id already exists
                        userTable.set(
                                userTable.indexOf(u),
                                user
                        );
                        System.out.println(user.toString());
                        set.set(true);
                    }
                });
        return set.get() ? user : null;
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
