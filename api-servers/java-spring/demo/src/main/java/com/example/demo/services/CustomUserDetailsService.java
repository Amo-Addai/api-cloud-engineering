package com.example.demo.services;


import com.example.demo.databases.entities.CustomUser;
import com.example.demo.databases.entities.UserEntity;
import com.example.demo.databases.repositories.OAuthDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService {// implements UserDetailsService {

    @Autowired
    OAuthDAO oathDao;

//    @Override
    public CustomUser loadUserByUsername(final String username)
            throws Exception {// UsernameNotFoundException {
            UserEntity userEntity = null;
            try {
                userEntity = oathDao.getUserDetails(username);
                CustomUser customUser = new CustomUser(userEntity);
                return customUser;
            } catch (Exception e) {
                e.printStackTrace();
                throw new Exception(// UsernameNotFoundException(
                        "User "
                        + username
                        + " was not found in the database"
                );
            }
    }

}
