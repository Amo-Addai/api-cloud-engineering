package com.example.demo.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebSecurity // TODO: Add 'spring-boot-starter-security' to classpath
public class WebSecurityConfiguration {// extends WebSecurityConfigurerAdapter {

    /* TODO: Solve Dependencies & any Deprecations

//    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/", "/home")
                .permitAll()
                .anyRequest()
                .authenticated()
            .and().formLogin()
                .loginPage("/login")
                .permitAll()
            .and().logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(
            AuthenticationManagerBuilder auth
    ) throws Exception {
        auth.inMemoryAuthentication()
            .withUser("user")
            .password("password")
            .roles("USER");
    }

    */

}
