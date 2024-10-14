package com.example.demo.databases.entities;

import java.util.ArrayList;
import java.util.Collection;

public class UserEntity {

    private String username;
    private String password;

    private Collection<
            String // GrantedAuthority - deprecated ?
            > grantedAuthoritiesList =
            new ArrayList<>();

    public UserEntity() {}

    public UserEntity(String username, String password, Collection<String> grantedAuthoritiesList) {
        this.username = username;
        this.password = password;
        this.grantedAuthoritiesList = grantedAuthoritiesList;
    }

    public Collection<String> getGrantedAuthoritiesList() {
        return grantedAuthoritiesList;
    }

    public void setGrantedAuthoritiesList(Collection<String> grantedAuthoritiesList) {
        this.grantedAuthoritiesList = grantedAuthoritiesList;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
