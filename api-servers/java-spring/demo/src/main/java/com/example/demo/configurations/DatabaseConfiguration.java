package com.example.demo.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@Configuration
public class DatabaseConfiguration {

    @Qualifier("jdbcProductService")
    @Autowired
    @Lazy  // Lazy-load this dependency to break the circular reference
    JdbcTemplate jdbcProductTemplate;

    @Qualifier("jdbcUserService")
    @Autowired
    @Lazy  // Lazy-load this dependency to break the circular reference
    JdbcTemplate jdbcUserTemplate;

    // * Beans' Qualifiers should be 'Canonically'-named
    // 'Canonical names' should be kebab-case ('-' separated), lowercase alpha-numeric characters and must start with a letter
    @Bean(name="dbproductservice")
    @ConfigurationProperties(prefix="spring.dbproductservice")
    @Primary
    public DataSource createProductServiceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="dbuserservice")
    @ConfigurationProperties(prefix="spring.dbuserservice")
    public DataSource createUserServiceDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="jdbcProductService")
    @Autowired
    public JdbcTemplate createJdbcTemplate_ProductService(
            @Qualifier("dbproductservice") DataSource productServiceDS
    ) {
        return new JdbcTemplate(productServiceDS);
    }

    @Bean(name="jdbcUserService")
    @Autowired
    public JdbcTemplate createJdbcTemplate_UserService(
            @Qualifier("dbuserservice") DataSource userServiceDS
    ) {
        return new JdbcTemplate(userServiceDS);
    }

}