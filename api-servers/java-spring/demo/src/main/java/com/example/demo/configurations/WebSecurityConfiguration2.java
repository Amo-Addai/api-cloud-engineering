package com.example.demo.configurations;

import com.example.demo.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableWebSecurity // TODO: Add 'spring-boot-starter-security' to classpath
//@EnableGlobalMethodSecurity(prePostEnabled=true)
public class WebSecurityConfiguration2 {// extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /* TODO: Solve Dependencies & any Deprecations

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    @Override
    protected void configure(
            AuthenticationManagerBuilder auth
    ) throws Exception {
        auth.userDetailsService(
                customUserDetailsService
        ).passwordEncoder(encoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .authenticated()
            .and().sessionManagement()
                .sessionCreationPolicy(
                        SessionCreationPolicy.NEVER
                );
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring();
    }

    @Bean
    @Override
    public AuthenticationManger authenticationManagerBean()
        throws Exception {
        return super.authenticationManagerBean();
    }

    */

}
