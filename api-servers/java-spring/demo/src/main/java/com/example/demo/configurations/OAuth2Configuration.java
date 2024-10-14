package com.example.demo.configurations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class OAuth2Configuration {// extends AuthorizationServerConfigurerAdapter {

    // TODO: work with OAuth2AuthorizationServerAutoConfiguration

    private String clientId = "client-id";
    private String clientSecret = "secret-key";
    private String privateKey = "private-key";
    private String publicKey = "public-key";

    /*

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
        JwtAccessTokenConverter converter
                = new JwtAccessTokenConverter();
        converter.setSigningKey(privateKey);
        converter.setVerifierKey(publicKey);
        return converter;
    }

    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(tokenEnhancer());
    }

    @Override
    public void configure(
            AuthorizationServerEndpointsConfigurer endpoints
    ) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                 .tokenStore(tokenStore())
                 .accessTokenConverter(tokenEnhancer());
    }

    @Override
    public void configure(
            AuthorizationServerSecurityConfigurer security
    ) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    public void configure(
            ClientDetailsServiceConfigurer clients
    ) throws Exception {
        clients
                .inMemory()
                .withClient(clientId)
                .secret(clientSecret)
                .scopes("read", "write")
                .authorizedGrantTypes(
                        "password",
                        "refresh_token"
                )
                .accessTokenValiditySeconds(20000)
                .refreshTokenValidity(20000);
    }

    */


    /* TODO: now, create a private & public key with openssl


    - generate private key

    openssl genrsa -out jwt.pem 2048
    openssl rsa -in jwt.pem

    - generate public key

    openssl rsa -in jwt.pem -pubout

     */

}
