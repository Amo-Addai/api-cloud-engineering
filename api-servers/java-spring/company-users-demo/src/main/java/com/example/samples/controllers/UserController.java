package com.example.samples.controllers;

import com.example.samples.models.Login;
import com.example.samples.models.User;
import com.example.samples.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path="/users/login")
    public Boolean auth(@RequestBody Login login) {
        return userService.auth(login);
    }

    @GetMapping(path="/users")
    public List<User> getAll() {
        return userService.findAll();
    }

    @PostMapping(path="/users")
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

}
