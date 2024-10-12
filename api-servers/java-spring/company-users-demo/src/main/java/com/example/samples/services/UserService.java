package com.example.samples.services;

import com.example.samples.models.Login;
import com.example.samples.models.User;
import com.example.samples.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Boolean auth(Login login) {
        return this.userRepository.auth(login);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User find(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

}
