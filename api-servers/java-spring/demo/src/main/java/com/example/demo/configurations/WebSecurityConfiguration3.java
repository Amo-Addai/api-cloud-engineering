package com.example.demo.configurations;

import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableOAuth2Sso // TODO: Add 'spring-boot-starter-security' to classpath
public class WebSecurityConfiguration3 {// extends WebSecurityConfigurerAdapter {

    /*

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/index.html")
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    */

}
