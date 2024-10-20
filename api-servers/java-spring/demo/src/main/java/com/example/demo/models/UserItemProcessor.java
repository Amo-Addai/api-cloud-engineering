package com.example.demo.models;

import com.example.demo.pojo.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserItemProcessor
    {// implements ItemProcessor<User, User> {

    private static final Logger log =
            LoggerFactory.getLogger(UserItemProcessor.class);

//    @Override
    public User process(final User user) // from pojo.models.User (not models.Users.User)
        throws Exception {
        final String firstName =
                user.getFirstName()
                    .toUpperCase();
        final String lastName =
                user.getLastName()
                    .toUpperCase();

        final User transformedPerson =
                new User(firstName, lastName);

        log.info(
                "Converting ("
                + user.toString()
                + ") into ("
                + transformedPerson.toString()
                + ")"
        );
        return transformedPerson;
    }

}
