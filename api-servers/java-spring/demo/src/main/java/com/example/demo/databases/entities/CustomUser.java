package com.example.demo.databases.entities;


public class CustomUser extends UserEntity {

    private static final long serialVersionUID = 1L;

    public CustomUser(UserEntity user) {
        super(user.getUsername(), user.getPassword(), user.getGrantedAuthoritiesList());
    }

}
