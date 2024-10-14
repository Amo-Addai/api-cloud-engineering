package com.example.demo.databases.repositories;

import com.example.demo.databases.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/*
@Repository class to read the User info from the db & send it to the Custom user service
& add the granted authority "ROLE_SYSTEMADMIN"
 */

@Repository
public class OAuthDAO {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public UserEntity getUserDetails(String username) {
        Collection<
                String // TODO: GrantedAuthority
                > grantedAuthoritiesList =
                new ArrayList<>();

        String sql = "SELECT * FROM USERS WHERE USERNAME=?";
        List<UserEntity> list = jdbcTemplate.query(
                sql,
                new String[] { username },
                (ResultSet rs, int rowNum) -> {
                    UserEntity user = new UserEntity();
                    user.setUsername(username);
                    user.setPassword(
                            rs.getString("PASSWORD")
                    );
                    return user;
                }
        );
        if (list.size() > 0) {
            /* TODO: GrantedAuthority - deprecated ?
            GrantedAuthority grantedAuthority =
                    new SimpleGrantedAuthority("ROLE_SYSTEMADMIN");
            grantedAuthoritiesList.add(grantedAuthority);
            */
            list.get(0)
                .setGrantedAuthoritiesList(
                        grantedAuthoritiesList
                );
            return list.get(0);
        }
        return null;
    }

}
