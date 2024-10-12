package com.example.samples.repositories;

import com.example.samples.models.Login;
import com.example.samples.models.User;

import java.util.List;

public interface UserRepository {

    Boolean auth(Login login);
    User save(User user);
    User findById(Long id);
    List<User> findAll();

}
