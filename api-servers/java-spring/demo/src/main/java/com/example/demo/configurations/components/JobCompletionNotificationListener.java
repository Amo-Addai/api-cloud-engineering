package com.example.demo.configurations.components;

import com.example.demo.DemoApplication;

import com.example.demo.pojo.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class JobCompletionNotificationListener
    {// extends JobExecutionListenerSupport {

    // to notify after BatchConfiguration job completion

    private static final Logger log =
            DemoApplication.getLogger();

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /*

//    @Override
    public void afterJob(JobExecution jobExecution) {
        if (
                jobExecution.getStatus()
                ==
                BatchStatus.COMPLETED
        ) {
            log.info(
                    "!!! Job Finished.\n"
                    + "Time to verify results"
            );
            List<User> results =
                    jdbcTemplate.query(
                            "SELECT first_name, last_name FROM USERS",
                            new RowMapper<User>() {
                                @Override
                                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                                    return new User(
                                            rs.getString(1),
                                            rs.getString(2)
                                    );
                                }
                            }
                    );
            results.stream()
                    .forEach((User user) -> {
                        log.info(
                                "Found <"
                                +  user.toString()
                                + "> in the database"
                        );
                    });
        }
    }

    */

}
